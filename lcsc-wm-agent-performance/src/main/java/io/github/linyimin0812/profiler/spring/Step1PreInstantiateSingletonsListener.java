package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import lombok.Data;
import org.kohsuke.MetaInfServices;

import java.util.Stack;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class Step1PreInstantiateSingletonsListener extends AbstractListener {

    private static final ThreadLocal<Stack<DoPreInstantiateSingletonsState>> startingPreInstantiate = ThreadLocal.withInitial(Stack::new);

    private static final String DEFAULT_LISTABLE_BEAN_FACTORY = "org.springframework.beans.factory.support.DefaultListableBeanFactory";

    static boolean isReady() {
        return !startingPreInstantiate.get().isEmpty();
    }

    static void startingInstantiate(String beanName) {
        startingPreInstantiate.get().peek().getGetSingletonBeans().push(beanName);
    }

    static String latestStartedInstantiate() {
        return startingPreInstantiate.get().peek().getGetSingletonBeans().peek();
    }

    @Override
    protected String listenClassName() {
        return DEFAULT_LISTABLE_BEAN_FACTORY;
    }

    @Override
    protected String listenMethodName() {
        return "preInstantiateSingletons";
    }

    @Override
    protected String[] listenMethodTypes() {
        return EMPTY;
    }

    @Override
    protected void atEnter(Event event) {
        Stack<DoPreInstantiateSingletonsState> stateStack = startingPreInstantiate.get();
        stateStack.push(new DoPreInstantiateSingletonsState());
    }

    @Override
    protected void atExit(Event event) {
        Stack<DoPreInstantiateSingletonsState> stateStack = startingPreInstantiate.get();
        DoPreInstantiateSingletonsState pop = stateStack.pop();
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
