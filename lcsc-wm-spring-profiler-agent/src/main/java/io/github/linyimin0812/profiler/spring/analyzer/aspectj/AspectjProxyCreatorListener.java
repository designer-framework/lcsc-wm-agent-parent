package io.github.linyimin0812.profiler.spring.analyzer.aspectj;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractMethodInvokeDetailListener;
import org.kohsuke.MetaInfServices;

@MetaInfServices(EventListener.class)
public class AspectjProxyCreatorListener extends AbstractMethodInvokeDetailListener {

    private static final String[] LISTEN_METHOD_TYPES = new String[]{"java.lang.Object", "java.lang.String", "java.lang.Object"};

    @Override
    protected String listenClassName0() {
        return "org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator";
    }

    @Override
    protected String listenMethodName() {
        return "wrapIfNecessary";
    }

    @Override
    protected String[] listenMethodTypes() {
        return LISTEN_METHOD_TYPES;
    }

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "Aop加载耗时[生成代理类]";
    }

}
