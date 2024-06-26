package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.ui.BeanInitResult;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.extension.enhance.springbean.PersistentThreadLocal;
import lombok.SneakyThrows;
import org.kohsuke.MetaInfServices;

import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class Step3SmartInitializingSingletonListener extends AbstractListener {

    private static final String SMART_INITIALIZING_SINGLETON_CLASS = "org.springframework.beans.factory.SmartInitializingSingleton";

    private final PersistentThreadLocal<Stack<BeanInitResultWrapper>> smartInitializingBeanThreadLocal = new PersistentThreadLocal<>(Stack::new);

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
        //记录bean初始化开始
        if (isReady()) {
            smartInitializingBeanThreadLocal.get().push(new BeanInitResultWrapper(Step1PreInstantiateSingletonsListener.latestStartedInstantiate(), false));
        }
    }

    @Override
    protected void atExit(Event event) {
        //记录bean初始化开始
        if (isReady()) {

            //是否SmartInitializingSingleton的实例
            if (isSmartInitializingSingletonClass((AtExitEvent) event)) {
                BeanInitResultWrapper beanInitResultWrapper = smartInitializingBeanThreadLocal.get().peek();
                beanInitResultWrapper.isPreInstantiateSingletons = true;
                beanInitResultWrapper.duration();
            }

        }
    }

    @SneakyThrows
    private boolean isSmartInitializingSingletonClass(InvokeEvent invokeEvent) {
        Class<?> smartInitializingSingletonClass = Class.forName(SMART_INITIALIZING_SINGLETON_CLASS, true, invokeEvent.clazz.getClassLoader());
        return smartInitializingSingletonClass.isAssignableFrom(invokeEvent.target.getClass());
    }

    private boolean isReady() {
        return Step1PreInstantiateSingletonsListener.isReady();
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
