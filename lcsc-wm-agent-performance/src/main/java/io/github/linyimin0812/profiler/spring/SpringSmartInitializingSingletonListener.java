package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class SpringSmartInitializingSingletonListener implements EventListener {

    static final ThreadLocal<Boolean> starting = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public boolean filter(String className) {
        return "org.springframework.beans.factory.support.DefaultListableBeanFactory".equals(className);
    }

    @Override
    public void onEvent(Event event) {
        if (event.type == Event.Type.AT_ENTER) {
            starting.set(Boolean.TRUE);
        } else if (event.type == Event.Type.AT_EXIT || event.type == Event.Type.AT_EXCEPTION_EXIT) {
            starting.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        String listenMethodName = "preInstantiateSingletons";
        String[] listenMethodTypes = new String[]{};
        if (!listenMethodName.equals(methodName)) {
            return false;
        }
        return methodTypes != null && listenMethodTypes.length == methodTypes.length;
    }

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info(SpringSmartInitializingSingletonListener.class, "============SpringSmartInitializingSingletonListener start=============");
    }

    @Override
    public void stop() {
        logger.info(SpringSmartInitializingSingletonListener.class, "============SpringSmartInitializingSingletonListener stop=============");
        starting.remove();
    }

}
