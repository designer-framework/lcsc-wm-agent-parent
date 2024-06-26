package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class AbstractListener implements EventListener {

    protected static final String[] EMPTY = new String[0];

    protected final Logger logger = LogFactory.getStartupLogger();

    @Override
    public boolean filter(String className) {
        return className.startsWith(listenClassName());
    }

    @Override
    public void onEvent(Event event) {
        if (event.type == Event.Type.AT_ENTER) {
            atEnter(event);
        } else if (event.type == Event.Type.AT_EXIT || event.type == Event.Type.AT_EXCEPTION_EXIT) {
            atExit(event);
        }
    }

    protected abstract void atEnter(Event event);

    protected abstract void atExit(Event event);

    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        String[] listenMethodTypes = listenMethodTypes();

        if (!methodName.equals(listenMethodName())) {
            return false;
        }
        if (methodTypes == null || listenMethodTypes.length != methodTypes.length) {
            return false;
        }

        for (int i = 0; i < listenMethodTypes.length; i++) {
            if (!listenMethodTypes[i].equals(methodTypes[i])) {
                return false;
            }
        }
        return true;
    }

    protected abstract String listenClassName();

    protected abstract String listenMethodName();

    protected abstract String[] listenMethodTypes();

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info(getClass(), "============" + getClass().getSimpleName() + " start=============");
    }

    @Override
    public void stop() {
        logger.info(getClass(), "============" + getClass().getSimpleName() + " stop=============");
    }

}
