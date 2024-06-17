package com.lcsc.wm.agent.clazz;

import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeCounterInterceptor;
import com.lcsc.wm.agent.utils.ClassDefinitionUtils;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 17:42
 */
public class SpringInvokeTimerClassFileTransformer extends SimpleClassFileTransformer {

    /**
     * @param className "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"
     */
    public SpringInvokeTimerClassFileTransformer(String... className) {
        super(className);
    }

    @Override
    protected byte[] transform(byte[] classfileBuffer) {
        return ClassDefinitionUtils.enhanceTimerCounter(classfileBuffer, SpringBeanCreateTimeCounterInterceptor.class);
    }

}
