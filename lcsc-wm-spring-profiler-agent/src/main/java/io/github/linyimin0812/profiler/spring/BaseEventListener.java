package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class BaseEventListener implements EventListener {

    protected final Logger logger = LogFactory.getStartupLogger();

    @Override
    public void start() {
        logger.error(getClass(), "============" + getClass().getSimpleName() + " start=============");
    }

    /**
     * @param methodName  方法名
     * @param methodTypes 方法参数列表
     * @return
     */
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

    protected abstract String listenMethodName();

    protected abstract String[] listenMethodTypes();

    @Override
    public void onEvent(Event event) {
        if (event.type == Event.Type.AT_ENTER) {
            if (atEnterIsReady(event)) {
                atEnter((AtEnterEvent) event);
            }
        } else if (event.type == Event.Type.AT_EXIT || event.type == Event.Type.AT_EXCEPTION_EXIT) {
            if (atExitIsReady(event)) {
                atExit((InvokeEvent) event);
            }
        }
    }

    protected boolean atEnterIsReady(Event event) {
        return isReady(event);
    }

    protected boolean atExitIsReady(Event event) {
        return isReady(event);
    }

    protected boolean isReady(Event event) {
        return true;
    }

    protected abstract void atEnter(AtEnterEvent event);

    protected abstract void atExit(InvokeEvent event);

    @Override
    public List<Event.Type> listen() {
        return Arrays.asList(Event.Type.AT_ENTER, Event.Type.AT_EXIT, Event.Type.AT_EXCEPTION_EXIT);
    }

    @Override
    public void stop() {
        logger.error(getClass(), "============" + getClass().getSimpleName() + " stop=============");
    }

}