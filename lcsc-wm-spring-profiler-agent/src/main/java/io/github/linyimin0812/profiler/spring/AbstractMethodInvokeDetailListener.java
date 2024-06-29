package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class AbstractMethodInvokeDetailListener extends AbstractInvokeDetailListener {

    @Override
    protected String[] listenClassName() {
        return new String[]{listenClassName0()};
    }

    protected abstract String listenClassName0();

    @Override
    protected String getFullyQualifiedName(AtEnterEvent atEnterEvent) {
        return listenClassName0() + "." + listenMethodName();
    }

}
