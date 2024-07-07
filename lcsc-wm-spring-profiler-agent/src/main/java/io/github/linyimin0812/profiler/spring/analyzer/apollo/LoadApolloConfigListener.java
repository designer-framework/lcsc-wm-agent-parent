package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractMethodInvokeDetailListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class LoadApolloConfigListener extends AbstractMethodInvokeDetailListener {

    private static final String LISTEN_CLASS_NAME = "com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer";

    @Override
    protected final String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "加载Apollo耗时[常规]";
    }

    @Override
    protected String listenClassName0() {
        return LISTEN_CLASS_NAME;
    }

    @Override
    protected String listenMethodName() {
        return "initialize";
    }

    @Override
    protected String[] listenMethodTypes() {
        return new String[]{"org.springframework.core.env.ConfigurableEnvironment"};
    }

}
