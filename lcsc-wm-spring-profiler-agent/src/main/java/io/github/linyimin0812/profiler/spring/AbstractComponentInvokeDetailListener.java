package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;

/**
 * @description: 匹配全限定类名 + 单个方法名 + 固定类型入参, 简而言之就是只能匹配一个类中的一个方法
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class AbstractComponentInvokeDetailListener extends AbstractInvokeDetailListener {

    @Override
    public final boolean filter(String className) {
        return super.filter(className);
    }

    @Override
    protected abstract String[] listenClassName();

    @Override
    protected abstract String getFullyQualifiedName(AtEnterEvent atEnterEvent);

    @Override
    protected abstract String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent);

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        super.atEnter(atEnterEvent);
    }

    @Override
    protected void atExit(InvokeEvent atExitEvent) {
        super.atExit(atExitEvent);
    }

    @Override
    public final boolean filter(String methodName, String[] methodTypes) {
        return true;
    }

    //以下所有过滤规则都没有意义
    @Override
    protected final String listenMethodName() {
        return null;
    }

    @Override
    protected final String[] listenMethodTypes() {
        return null;
    }

}
