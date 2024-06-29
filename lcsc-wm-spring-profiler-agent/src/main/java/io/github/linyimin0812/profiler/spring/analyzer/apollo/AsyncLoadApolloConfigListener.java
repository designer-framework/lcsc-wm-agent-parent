package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import org.kohsuke.MetaInfServices;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class AsyncLoadApolloConfigListener extends LoadApolloConfigListener {

    private static final String[] listenClassName = new String[]{"com.lcsc.performance.apollo.AsyncApolloApplicationContextInitializer"};

    @Override
    protected String[] listenClassName() {
        return listenClassName;
    }

}
