package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.spring.AbstractListener;
import org.kohsuke.MetaInfServices;

import java.util.Stack;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class ApolloLoaderConfigStep1Listener extends AbstractListener {

    private static final ThreadLocal<Stack<Boolean>> initializeConfig = ThreadLocal.withInitial(Stack::new);

    static boolean nodeIsReady() {
        return !initializeConfig.get().isEmpty();
    }

    @Override
    protected void atEnter(AtEnterEvent event) {
        initializeConfig.get().push(Boolean.TRUE);
    }

    @Override
    protected void atExit(AtExitEvent event) {
        initializeConfig.get().pop();
    }

    @Override
    protected String listenClassName() {
        return "com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer";
    }

    @Override
    protected String listenMethodName() {
        return "initialize";
    }

    @Override
    protected String[] listenMethodTypes() {
        return new String[]{"org.springframework.context.ConfigurableApplicationContext"};
    }

}
