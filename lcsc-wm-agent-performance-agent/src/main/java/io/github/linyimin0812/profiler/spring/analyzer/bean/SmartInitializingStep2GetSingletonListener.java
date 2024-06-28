package io.github.linyimin0812.profiler.spring.analyzer.bean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.spring.AbstractListener;
import org.kohsuke.MetaInfServices;

import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(String)
 */
@MetaInfServices(EventListener.class)
public class SmartInitializingStep2GetSingletonListener extends AbstractListener {

    private static final List<String> DEFAULT_SINGLETON_BEAN_REGISTRY = Collections.singletonList("org.springframework.beans.factory.support.DefaultSingletonBeanRegistry");

    static boolean nodeIsReady() {
        return SmartInitializingStep1PreInstantiateListener.nodeIsReady();
    }

    @Override
    protected boolean isReady(Event event) {
        return nodeIsReady();
    }

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        SmartInitializingStep1PreInstantiateListener.startingInstantiate((String) atEnterEvent.args[0]);
    }

    @Override
    protected void atExit(InvokeEvent event) {
        //ignore
    }

    @Override
    protected List<String> listenClassName() {
        return DEFAULT_SINGLETON_BEAN_REGISTRY;
    }

    @Override
    protected String listenMethodName() {
        return "getSingleton";
    }

    @Override
    protected String[] listenMethodTypes() {
        return new String[]{"java.lang.String"};
    }

}
