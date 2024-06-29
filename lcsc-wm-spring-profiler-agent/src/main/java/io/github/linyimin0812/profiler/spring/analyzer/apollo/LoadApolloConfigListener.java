package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.spring.AbstractComponentInvokeDetailListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class LoadApolloConfigListener extends AbstractComponentInvokeDetailListener {

    private static final String INVOKE_COUNT_METHOD_ALIAS = "阿波罗配置加载耗时";

    private static final String[] LISTEN_CLASS_NAME = new String[]{"com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer"};

    @Override
    protected final String getInvokeCountMethodAlias(AtEnterEvent atEnterEvent) {
        return INVOKE_COUNT_METHOD_ALIAS;
    }

    @Override
    protected String[] listenClassName() {
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
