package io.github.linyimin0812.profiler.spring;

import org.apache.commons.lang3.StringUtils;

/**
 * @description: 匹配全限定类名 + 单个方法名 + 固定类型入参, 简而言之就是只能匹配一个类中的一个方法
 * @author: Designer
 * @date : 2024-06-25 21:06
 */
public abstract class AbstractInvokeDetailListener extends BaseEventListener {

    /**
     * @param className 类全限定名, 如果为空, 默认返回为true
     * @return
     */
    @Override
    public boolean filter(String className) {
        return StringUtils.startsWith(className, listenClassName());
    }

    protected abstract String listenClassName();

}
