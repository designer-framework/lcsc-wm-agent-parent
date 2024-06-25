package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.ui.BeanInitResult;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.extension.enhance.springbean.PersistentThreadLocal;
import org.kohsuke.MetaInfServices;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class _3SmartInitializingSingletonListener extends _0AbstractListener {

    private static final List<BeanInitResult> beanInitResultList = new ArrayList<>();

    private final PersistentThreadLocal<Stack<BeanInitResult>> profilerResultThreadLocal = new PersistentThreadLocal<>(Stack::new);

    @Override
    protected String listenClassName() {
        return "com.designer";
    }

    @Override
    protected String listenMethodName() {
        return "afterSingletonsInstantiated";
    }

    @Override
    protected String[] listenMethodTypes() {
        return EMPTY;
    }

    @Override
    protected void atEnter(Event event) {
        // 记录bean初始化开始
        if (at()) {
            String beanName = _1PreInstantiateSingletonsListener.getSingletonBeans.peek();
            BeanInitResult beanInitResult = new BeanInitResult(beanName);

            beanInitResultList.add(beanInitResult);

            if (!profilerResultThreadLocal.get().isEmpty()) {

                BeanInitResult parentBeanInitResult = profilerResultThreadLocal.get().peek();
                parentBeanInitResult.addChild(beanInitResult);

            }

            profilerResultThreadLocal.get().push(beanInitResult);
        }
    }

    @Override
    protected void atExit(Event event) {
        // 记录bean初始化开始
        if (_2GetSingletonListener.at()) {
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

    @Override
    public void stop() {
        super.stop();

        Map<String, BeanInitResult> beanInitResultMap = StartupVO.getBeanInitResultList().stream().collect(Collectors.toMap(BeanInitResult::getName, Function.identity(), (beanInitResult, beanInitResult2) -> {
            return beanInitResult2;
        }));

        List<BeanInitResult> beanInitResultList = _3SmartInitializingSingletonListener.beanInitResultList;
        beanInitResultList.forEach(beanInitResult_ -> {

            beanInitResultMap.computeIfPresent(beanInitResult_.getName(), (s, beanInitResult) -> {

                beanInitResult.getTags().put("AfterSingletonsInstantiated", beanInitResult_.getActualDuration() + "/ms");
                return beanInitResult;
                
            });

        });
    }

    private boolean at() {
        return _2GetSingletonListener.at() && !_1PreInstantiateSingletonsListener.getSingletonBeans.isEmpty();
    }

}
