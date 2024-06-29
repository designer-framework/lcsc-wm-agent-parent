package io.github.linyimin0812.profiler.spring;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.InvokeEvent;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetailExtend;
import io.github.linyimin0812.profiler.common.ui.StartupVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 匹配全限定类名 + 单个方法名 + 固定类型入参, 简而言之就是只能匹配一个类中的一个方法
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
abstract class AbstractInvokeDetailListener extends BaseEventListener {

    protected Map<String, MethodInvokeDetailExtend> INVOKE_DETAIL_MAP = new ConcurrentHashMap<>();

    /**
     * @param methodName  方法名
     * @param methodTypes 方法参数列表
     * @return
     */
    @Override
    public boolean filter(String methodName, String[] methodTypes) {
        return super.filter(methodName, methodTypes);
    }

    @Override
    protected abstract String listenMethodName();

    @Override
    protected void atEnter(AtEnterEvent atEnterEvent) {
        INVOKE_DETAIL_MAP.put(
                getEventUniqueKey(atEnterEvent)
                , new MethodInvokeDetailExtend(doGetInvokeCountMethodAlias(atEnterEvent), atEnterEvent.args)
        );
    }

    private String doGetInvokeCountMethodAlias(AtEnterEvent atEnterEvent) {
        return getFullyQualifiedNameAlias(atEnterEvent) + ": " + getFullyQualifiedName(atEnterEvent);
    }

    protected abstract String getFullyQualifiedName(AtEnterEvent atEnterEvent);

    protected abstract String getFullyQualifiedNameAlias(AtEnterEvent atEnterEvent);

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
