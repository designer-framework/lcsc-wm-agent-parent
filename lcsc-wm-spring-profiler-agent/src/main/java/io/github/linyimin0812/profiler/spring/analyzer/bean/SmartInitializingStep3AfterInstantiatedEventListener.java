package io.github.linyimin0812.profiler.spring.analyzer.bean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.ui.BeanInitResult;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.extension.enhance.springbean.PersistentThreadLocal;
import io.github.linyimin0812.profiler.spring.BaseEventListener;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
 */
@MetaInfServices(EventListener.class)
public class SmartInitializingStep3AfterInstantiatedEventListener extends BaseEventListener {

    private static final String[] DEFAULT_SINGLETON_BEAN_REGISTRY = {"com.designer"};

    private final PersistentThreadLocal<Stack<BeanInitResultWrapper>> smartInitializingBeanThreadLocal = new PersistentThreadLocal<>(Stack::new);

    @Override
    protected void atEnter(AtEnterEvent event) {
        //记录bean初始化开始
        if (isReady()) {
            smartInitializingBeanThreadLocal.get().push(new BeanInitResultWrapper(SmartInitializingStep1PreInstantiateEventListener.latestStartedInstantiate(), false));
        }
    }

    @Override
    protected void atExit(InvokeEvent event) {
        //记录bean初始化开始
        if (isReady()) {

            //是否SmartInitializingSingleton的实例
            if (isSmartInitializingSingletonClass(event)) {
                BeanInitResultWrapper beanInitResultWrapper = smartInitializingBeanThreadLocal.get().peek();
                beanInitResultWrapper.isPreInstantiateSingletons = true;
                beanInitResultWrapper.duration();
            }

        }
    }

    @Override
    protected String[] listenClassName() {
        return DEFAULT_SINGLETON_BEAN_REGISTRY;
    }

    @Override
    protected String listenMethodName() {
        return "afterSingletonsInstantiated";
    }

    @Override
    protected String[] listenMethodTypes() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @SneakyThrows
    private boolean isSmartInitializingSingletonClass(InvokeEvent invokeEvent) {
        return Class.forName("org.springframework.beans.factory.SmartInitializingSingleton", true, invokeEvent.clazz.getClassLoader())
                .isAssignableFrom(invokeEvent.target.getClass());
    }

    private boolean isReady() {
        return SmartInitializingStep2GetSingletonEventListener.nodeIsReady();
    }

    @Override
    public void stop() {
        super.stop();

        Map<String, BeanInitResult> beanInitResultMap = StartupVO.getBeanInitResultList().stream().collect(Collectors.toMap(BeanInitResult::getName, Function.identity(), (beanInitResult, beanInitResult2) -> {
            return beanInitResult2;
        }));

        smartInitializingBeanThreadLocal.get().forEach(beanInitResultWrapper -> {

            beanInitResultMap.computeIfPresent(beanInitResultWrapper.getName(), (beaName, beanInitResult) -> {
                if (beanInitResultWrapper.isPreInstantiateSingletons) {
                    beanInitResult.getTags().put("AfterSingletonsInstantiated", beanInitResultWrapper.getActualDuration() + "/ms");
                }
                return beanInitResult;

            });

        });

        smartInitializingBeanThreadLocal.clear();
    }

    private static class BeanInitResultWrapper extends BeanInitResult {
        boolean isPreInstantiateSingletons;

        public BeanInitResultWrapper(String name, boolean isPreInstantiateSingletons) {
            super(name);
            this.isPreInstantiateSingletons = isPreInstantiateSingletons;
        }
    }

}
