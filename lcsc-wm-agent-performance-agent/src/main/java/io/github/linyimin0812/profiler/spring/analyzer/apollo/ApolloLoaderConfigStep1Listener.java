package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetailExtend;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.spring.AbstractListener;
import org.kohsuke.MetaInfServices;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
 **/
@MetaInfServices(EventListener.class)
public class ApolloLoaderConfigStep1Listener extends AbstractListener {

    private final Logger logger = LogFactory.getStartupLogger();

    protected Map<String /* processId_invokeId */, MethodInvokeDetailExtend> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        INVOKE_DETAIL_MAP.put(
                getEventUniqueKey(atEnterEvent)
                , new MethodInvokeDetailExtend(buildMethodQualifier(atEnterEvent), atEnterEvent.args, getInvokeCountMethodAlias())
        );
    }

    @Override
    protected void atExit(InvokeEvent atExitEvent) {
        String key = getEventUniqueKey(atExitEvent);

        if (INVOKE_DETAIL_MAP.containsKey(key)) {
            MethodInvokeDetailExtend invokeDetail = INVOKE_DETAIL_MAP.get(key);
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());

            StartupVO.addMethodInvokeDetail(invokeDetail);
        } else {
            logger.warn(ApolloLoaderConfigStep1Listener.class, "Key: {} does not exist in the Map, there may be an error.", key);
        }
    }

    private String buildMethodQualifier(InvokeEvent event) {
        return event.clazz.getSimpleName() + "." + event.methodName;
    }

    private String getFullName() {
        return listenClassName() + "." + listenMethodName() + "(" + String.join(",", listenMethodTypes()) + ")";
    }

    public String getInvokeCountMethodAlias() {
        return "阿波罗配置加载耗时";
    }

    private String getEventUniqueKey(InvokeEvent event) {
        return String.format("%s_%s", event.processId, event.invokeId);
    }

    @Override
    protected List<String> listenClassName() {
        return Arrays.asList(
                "com.lcsc.performance.apollo.AsyncApolloApplicationContextInitializer"
        );
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
