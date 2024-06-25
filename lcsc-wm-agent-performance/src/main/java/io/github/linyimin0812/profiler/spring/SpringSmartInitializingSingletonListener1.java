package io.github.linyimin0812.profiler.spring;

import com.google.gson.Gson;
import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.ui.BeanInitResult;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.common.utils.GsonUtil;
import io.github.linyimin0812.profiler.extension.enhance.springbean.PersistentThreadLocal;
import org.kohsuke.MetaInfServices;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class SpringSmartInitializingSingletonListener1 implements EventListener {

    private static final List<BeanInitResult> beanInitResultList = new ArrayList<>();

    private final Logger logger = LogFactory.getStartupLogger();

    private final PersistentThreadLocal<Stack<BeanInitResult>> profilerResultThreadLocal = new PersistentThreadLocal<>(Stack::new);

    private final Gson GSON = GsonUtil.create();

    @Override
    public boolean filter(String className) {
        return className.startsWith("io.github.lin");
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        List<String> listenMethodName = Collections.singletonList("afterSingletonsInstantiated");

        if (!listenMethodName.contains(methodName)) {
            return false;
        }

        return methodTypes != null && methodTypes.length == 0;
    }

    @Override
    public void onEvent(Event event) {
        //
        if (!SpringSmartInitializingSingletonListener.starting.get()) {
            return;
        }

        if (event.type == Event.Type.AT_ENTER) {
            AtEnterEvent atEnterEvent = (AtEnterEvent) event;
            // 记录bean初始化开始
            String beanName = (String) atEnterEvent.args[0];
            createBeanInitResult(beanName);

        } else if (event.type == Event.Type.AT_EXIT || event.type == Event.Type.AT_EXCEPTION_EXIT) {
            // bean初始化结束, 出栈

            AtExitEvent atExitEvent = (AtExitEvent) event;
            Map<String, String> tags = new HashMap<>();
            tags.put("threadName", Thread.currentThread().getName());
            tags.put("class", atExitEvent.returnObj == null ? null : atExitEvent.returnObj.getClass().getName());
            ClassLoader classLoader = atExitEvent.returnObj == null ? null : atExitEvent.returnObj.getClass().getClassLoader();
            tags.put("classloader", classLoader == null ? "boostrap" : classLoader.getClass().getSimpleName());

            BeanInitResult beanInitResult = profilerResultThreadLocal.get().pop();
            beanInitResult.setTags(tags);
            beanInitResult.duration();
        }
    }

    private void createBeanInitResult(String beanName) {

        BeanInitResult beanInitResult = new BeanInitResult(beanName);

        beanInitResultList.add(beanInitResult);

        if (!profilerResultThreadLocal.get().isEmpty()) {

            BeanInitResult parentBeanInitResult = profilerResultThreadLocal.get().peek();
            parentBeanInitResult.addChild(beanInitResult);

        }

        profilerResultThreadLocal.get().push(beanInitResult);
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info(SpringSmartInitializingSingletonListener1.class, "============SpringSmartInitializingSingletonListener start=============");
    }

    @Override
    public void stop() {
        logger.info(SpringSmartInitializingSingletonListener1.class, "============SpringSmartInitializingSingletonListener stop=============");

        List<BeanInitResult> remainInitResult = new ArrayList<>();

        for (Stack<BeanInitResult> stack : profilerResultThreadLocal.getAll()) {
            remainInitResult.addAll(stack);
        }

        if (!remainInitResult.isEmpty()) {
            try {
                logger.warn(SpringSmartInitializingSingletonListener1.class, "profilerResultThreadLocal is not empty. There may be a problem with the initialization of the bean. {}", GSON.toJson(remainInitResult));
            } catch (Throwable ignored) {
                List<String> beanNames = remainInitResult.stream().map(BeanInitResult::getName).collect(Collectors.toList());
                logger.warn(SpringSmartInitializingSingletonListener1.class, "profilerResultThreadLocal is not empty. There may be a problem with the initialization of the bean. {}", GSON.toJson(beanNames));
            }
        }

        Map<String, BeanInitResult> beanInitResultMap = StartupVO.getBeanInitResultList().stream().collect(Collectors.toMap(BeanInitResult::getName, Function.identity(), (beanInitResult, beanInitResult2) -> {
            return beanInitResult2;
        }));

        List<BeanInitResult> beanInitResultList = SpringSmartInitializingSingletonListener1.beanInitResultList;
        beanInitResultList.forEach(beanInitResult_ -> {

            beanInitResultMap.computeIfPresent(beanInitResult_.getName(), (s, beanInitResult) -> {
                beanInitResult.getTags().put("SmartInitializingSingletonCostTime", "" + beanInitResult.getActualDuration());
                return beanInitResult;
            });

        });
    }

}
