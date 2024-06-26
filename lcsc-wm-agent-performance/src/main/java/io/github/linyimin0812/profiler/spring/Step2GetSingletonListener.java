package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
@MetaInfServices(EventListener.class)
public class Step2GetSingletonListener extends AbstractListener {

    private static final String DEFAULT_SINGLETON_BEAN_REGISTRY = "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry";

    @Override
    protected String listenClassName() {
        return DEFAULT_SINGLETON_BEAN_REGISTRY;
    }

    @Override
    protected void atEnter(Event event) {
        if (isReady()) {
            AtEnterEvent atEnterEvent = (AtEnterEvent) event;
            Step1PreInstantiateSingletonsListener.startingInstantiate((String) atEnterEvent.args[0]);
        }
    }

    @Override
    protected void atExit(Event event) {
        if (isReady()) {
            //ignore
        }
    }

    boolean isReady() {
        return Step1PreInstantiateSingletonsListener.isReady();
    }

    @Override
    protected String listenMethodName() {
        return "getSingleton";
    }

    @Override
    protected String[] listenMethodTypes() {
        return new String[]{"java.lang.String"};
    }

    @Override
    public void stop() {
        super.stop();
    }

}
