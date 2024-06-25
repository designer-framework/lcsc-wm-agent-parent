package com.lcsc.wm.agent.utils;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.Decompiler;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeCounterInterceptor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 18:36
 */
public class ClassDefinitionUtils {

    public static ClassDefinition enhanceTimerCounterClassDefinition(Class<?> clazz, Class<?> interceptorClass) {
        return new ClassDefinition(clazz, enhanceTimerCounter(clazz, interceptorClass));
    }

    @SneakyThrows
    public static byte[] enhanceTimerCounter(Class<?> clazz, Class<?> interceptorClass) {
        return enhanceTimerCounter(AsmUtils.loadClass(clazz), interceptorClass);
    }

    public static byte[] enhanceTimerCounter(byte[] clazz, Class<?> interceptorClass) {
        return enhanceTimerCounter(AsmUtils.toClassNode(clazz), interceptorClass);
    }

    @SneakyThrows
    public static byte[] enhanceTimerCounter(ClassNode classNode, Class<?> interceptorClass) {
        // Parse the defined Interceptor class and related annotations
        DefaultInterceptorClassParser interceptorClassParser = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> interceptorProcessors = interceptorClassParser.parse(interceptorClass);

        // Enhanced process of loaded bytecodes
        for (MethodNode methodNode : classNode.methods) {
            MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
            for (InterceptorProcessor interceptor : interceptorProcessors) {
                interceptor.process(methodProcessor);
            }
        }
        // Get the enhanced bytecode
        byte[] bytes = AsmUtils.toBytes(classNode);

        // View decompilation results
        //System.out.println(Decompiler.decompile(bytes));

        return bytes;
    }

    public static void main(String[] args) throws IOException {
        byte[] bytes = enhanceTimerCounter(org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.class, SpringBeanCreateTimeCounterInterceptor.class);
        // View decompilation results
        System.out.println(Decompiler.decompile(bytes));
    }

}
