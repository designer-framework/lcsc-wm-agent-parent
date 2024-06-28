package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class AsyncApolloLoaderConfigStep1Listener extends ApolloLoaderConfigStep1Listener {

    @Override
    public String getInvokeCountMethodAlias() {
        return "Async阿波罗配置加载耗时";
    }

    @Override
    protected List<String> listenClassName() {
        return Arrays.asList(
                "com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer"
        );
    }

}
