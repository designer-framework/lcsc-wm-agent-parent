package com.lcsc.wm.agent.attach;

import com.lcsc.wm.agent.clazz.SpringInvokeTimerClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class PreMain {

    /**
     * @param agentArgs
     * @param instrumentation
     * @see sun.instrument.InstrumentationImpl#loadClassAndStartAgent(String, String, String)
     * 优先加载当前方法, 然后加载下面的方法
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) throws UnmodifiableClassException, ClassNotFoundException {
        //类加载之前, 重新定义Class
        instrumentation.addTransformer(new SpringInvokeTimerClassFileTransformer("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"), true);
        //不得添加、删除或重命名字段或方法、更改方法的签名或更改继承
        //instrumentation.redefineClasses(ClassDefinitionUtils.enhanceTimerCounterClassDefinition(AbstractAutowireCapableBeanFactory.class, SpringBeanCreateTimeCounterInterceptor.class));
        //inst.retransformClasses();
    }

    public static void premain(String agentArgs) {
        System.out.println("premain2");
    }

}
