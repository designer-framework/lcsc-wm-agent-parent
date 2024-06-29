package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetailExtend;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class AbstractComponentInvokeDetailListener extends BaseEventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    protected Map<String, MethodInvokeDetailExtend> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    /**
     * @param className 类全限定名集合, 如果为空, 默认返回为false
     * @return
     */
    @Override
    public boolean filter(String className) {
        return StringUtils.startsWithAny(className, listenClassName());
    }

    /**
     * @param methodName  方法名
     * @param methodTypes 方法参数列表
     * @return
     */
    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        return super.filter(methodName, methodTypes);
    }

    protected abstract String[] listenClassName();

    @Override
    protected abstract String listenMethodName();

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        INVOKE_DETAIL_MAP.put(
                getEventUniqueKey(atEnterEvent)
                , new MethodInvokeDetailExtend(getInvokeCountMethodAlias(atEnterEvent), atEnterEvent.args)
        );
    }

    protected abstract String getInvokeCountMethodAlias(AtEnterEvent atEnterEvent);

    @Override
    protected void atExit(InvokeEvent atExitEvent) {
        String key = getEventUniqueKey(atExitEvent);

        if (INVOKE_DETAIL_MAP.containsKey(key)) {
            MethodInvokeDetailExtend invokeDetail = INVOKE_DETAIL_MAP.get(key);
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());

            StartupVO.addMethodInvokeDetail(invokeDetail);
        } else {
            logger.warn(getClass(), "Key: {} does not exist in the Map, there may be an error.", key);
        }
    }

    protected String getEventUniqueKey(InvokeEvent event) {
        return String.format("%s_%s", event.processId, event.invokeId);
    }

}
