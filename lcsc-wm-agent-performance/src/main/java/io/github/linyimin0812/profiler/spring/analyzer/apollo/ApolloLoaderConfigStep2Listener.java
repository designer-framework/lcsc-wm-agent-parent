package io.github.linyimin0812.profiler.spring.analyzer.apollo;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetailExtend;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.spring.AbstractListener;
import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 * @see io.github.linyimin0812.profiler.extension.enhance.invoke.InvokeDetailListener
 **/
@MetaInfServices(EventListener.class)
public class ApolloLoaderConfigStep2Listener extends AbstractListener {

    private final Logger logger = LogFactory.getStartupLogger();

    protected Map<String /* processId_invokeId */, MethodInvokeDetailExtend> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    protected List<String> methodQualifiers = new ArrayList<>();

    @Override
    protected boolean isReady(Event event) {
        return ApolloLoaderConfigStep1Listener.nodeIsReady();
    }

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        INVOKE_DETAIL_MAP.put(
                getEventUniqueKey(atEnterEvent)
                , new MethodInvokeDetailExtend(buildMethodQualifier(atEnterEvent), atEnterEvent.args, getInvokeCountMethodAlias())
        );
    }

    @Override
    protected void atExit(AtExitEvent atExitEvent) {
        String key = getEventUniqueKey(atExitEvent);

        if (INVOKE_DETAIL_MAP.containsKey(key)) {
            MethodInvokeDetailExtend invokeDetail = INVOKE_DETAIL_MAP.get(key);
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());

            StartupVO.addMethodInvokeDetail(invokeDetail);
        } else {
            logger.warn(ApolloLoaderConfigStep2Listener.class, "Key: {} does not exist in the Map, there may be an error.", key);
        }

    }

    @Override
    protected String listenClassName() {
        return "com.ctrip.framework.apollo.ConfigService";
    }

    @Override
    protected String listenMethodName() {
        return "getConfig";
    }

    @Override
    protected String[] listenMethodTypes() {
        return EMPTY;
    }

    @Override
    public void start() {
        logger.info(ApolloLoaderConfigStep2Listener.class, "===============InvokeCountListener start==================");
        INVOKE_DETAIL_MAP.clear();
        logger.info(ApolloLoaderConfigStep2Listener.class, "config spring-startup-analyzer.invoke.count.methods is {}", getFullName());
    }

    private String getEventUniqueKey(InvokeEvent event) {
        return String.format("%s_%s", event.processId, event.invokeId);
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

    @Override
    public void stop() {
        logger.info(ApolloLoaderConfigStep2Listener.class, "===============InvokeCountListener stop==================");

        methodQualifiers.clear();

        INVOKE_DETAIL_MAP.clear();
    }

}
