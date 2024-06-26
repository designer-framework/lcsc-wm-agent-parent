package io.github.linyimin0812.profiler.spring.analyzer.bean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.spring.BaseEventListener;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.kohsuke.MetaInfServices;

import java.util.Stack;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()
 */
@MetaInfServices(EventListener.class)
public class SmartInitializingStep1PreInstantiateEventListener extends BaseEventListener {

    private static final ThreadLocal<Stack<DoPreInstantiateSingletonsState>> startingPreInstantiate = ThreadLocal.withInitial(Stack::new);

    private static final String[] DEFAULT_LISTABLE_BEAN_FACTORY = {"org.springframework.beans.factory.support.DefaultListableBeanFactory"};

    static boolean nodeIsReady() {
        return !startingPreInstantiate.get().isEmpty();
    }

    static void startingInstantiate(String beanName) {
        startingPreInstantiate.get().peek().getGetSingletonBeans().push(beanName);
    }

    static String latestStartedInstantiate() {
        return startingPreInstantiate.get().peek().getGetSingletonBeans().peek();
    }

    @Override
    protected void atEnter(AtEnterEvent event) {
        Stack<DoPreInstantiateSingletonsState> stateStack = startingPreInstantiate.get();
        stateStack.push(new DoPreInstantiateSingletonsState());
    }

    @Override
    protected void atExit(InvokeEvent event) {
        Stack<DoPreInstantiateSingletonsState> stateStack = startingPreInstantiate.get();
        DoPreInstantiateSingletonsState pop = stateStack.pop();
    }

    @Override
    protected String[] listenClassName() {
        return DEFAULT_LISTABLE_BEAN_FACTORY;
    }

    @Override
    protected String listenMethodName() {
        return "preInstantiateSingletons";
    }

    @Override
    protected String[] listenMethodTypes() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public void stop() {
        super.stop();
        startingPreInstantiate.remove();
    }

    @Data
    static class DoPreInstantiateSingletonsState {
        Stack<String> getSingletonBeans = new Stack<>();
    }

}
