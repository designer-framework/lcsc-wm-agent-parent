package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import org.kohsuke.MetaInfServices;

import java.util.Stack;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class _1PreInstantiateSingletonsListener extends _0AbstractListener {

    static final ThreadLocal<Boolean> startingPreInstantiate = ThreadLocal.withInitial(() -> Boolean.FALSE);

    static final Stack<String> getSingletonBeans = new Stack<>();

    private static final String DEFAULT_LISTABLE_BEAN_FACTORY = "org.springframework.beans.factory.support.DefaultListableBeanFactory";

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
        startingPreInstantiate.set(Boolean.TRUE);
    }

    @Override
    protected void atExit(Event event) {
        startingPreInstantiate.set(Boolean.FALSE);
        getSingletonBeans.clear();
    }

    @Override
    public void stop() {
        super.stop();
        startingPreInstantiate.remove();
    }

}
