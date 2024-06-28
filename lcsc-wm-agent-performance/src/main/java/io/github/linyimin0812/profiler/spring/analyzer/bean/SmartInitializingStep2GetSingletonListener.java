package io.github.linyimin0812.profiler.spring.analyzer.bean;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.spring.AbstractListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(String)
 */
@MetaInfServices(EventListener.class)
public class SmartInitializingStep2GetSingletonListener extends AbstractListener {

    private static final String DEFAULT_SINGLETON_BEAN_REGISTRY = "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry";

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
    protected void atExit(AtExitEvent event) {
        //ignore
    }

    @Override
    protected String listenClassName() {
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
