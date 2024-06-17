package com.lcsc.wm.agent.agent;

import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeCounterInterceptor;
import com.lcsc.wm.agent.utils.ClassDefinitionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;

import java.lang.instrument.Instrumentation;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 20:06
 */
@Data
@AllArgsConstructor
public class AgentLoader implements Runnable {

    private Instrumentation instrumentation;

    @SneakyThrows
    @Override
    public void run() {
        //类加载之前, 重新定义Class
        //instrumentation.addTransformer(new SpringInvokeTimerClassFileTransformer("org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"), true);
        //不得添加、删除或重命名字段或方法、更改方法的签名或更改继承
        instrumentation.redefineClasses(ClassDefinitionUtils.enhanceTimerCounterClassDefinition(AbstractAutowireCapableBeanFactory.class, SpringBeanCreateTimeCounterInterceptor.class));
        //inst.retransformClasses();
    }

}
