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
public class AsyncLoadApolloConfigListener extends AbstractMethodInvokeDetailListener {

    @Override
    protected String listenClassName0() {
        return "com.lcsc.turbo.apollo.AsyncPreloadApolloConfigEnvironmentPostProcessor";
    }

    @Override
    protected String listenMethodName() {
        return "concurrentInitializeConfig";
    }

    @Override
    protected String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent) {
        return "加载Apollo耗时[异步预加载]";
    }

    @Override
    protected String[] listenMethodTypes() {
        return new String[]{"java.util.List"};
    }

}
