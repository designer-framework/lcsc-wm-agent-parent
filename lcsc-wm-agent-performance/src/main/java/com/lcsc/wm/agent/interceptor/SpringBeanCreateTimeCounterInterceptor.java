package com.lcsc.wm.agent.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.lcsc.wm.agent.model.InvokeTrace;
import com.lcsc.wm.agent.queue.Node;
import com.lcsc.wm.agent.queue.Root;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Stack;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 19:12
 */
public class SpringBeanCreateTimeCounterInterceptor {

    /**
     * @param target
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param args
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(String, RootBeanDefinition, Object[])
     */
    @AtEnter(inline = false)
    public static void atEnter(@Binding.This Object target, @Binding.Class Class<?> clazz,
                               @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc,
                               @Binding.Args Object[] args
    ) {
        if ("doCreateBean".equals(methodName)) {
            System.out.println("创建Bean: " + args[0]);
            InvokeTrace invokeTrace = new InvokeTrace();

            //
            invokeTrace.beanName = (String) args[0];
            invokeTrace.start = System.currentTimeMillis();

            //
            Stack<InvokeTrace> invokeTraces = SpringBeanCreateTimeHolder.creatingBeanMap.computeIfAbsent(invokeTrace.beanName, beanName -> new Stack<>());
            invokeTraces.push(invokeTrace);

            Root creatingRoot = SpringBeanCreateTimeHolder.creatingRoot;

            //首次初始化
            if (creatingRoot == null) {

                Node headNode = new Node(invokeTrace);
                headNode.pre = headNode;

                Root creatingTree = (creatingRoot = (SpringBeanCreateTimeHolder.creatingRoot = new Root()));
                creatingTree.head = headNode;
                creatingTree.tail = headNode;

            } else {

                Node oldTailNode = creatingRoot.tail;

                Node newTailNode = new Node();
                newTailNode.trace = invokeTrace;
                newTailNode.pre = creatingRoot.tail;

                creatingRoot.tail = newTailNode;
                oldTailNode.next = newTailNode;

            }

            //入栈
            invokeTrace.setDeep(creatingRoot.deepUp());
            SpringBeanCreateTimeHolder.creatingBeanTrace.push(invokeTrace.beanName);
        }
    }

    @AtExit(inline = false)
    public static void atExit(@Binding.This Object target, @Binding.Class Class<?> clazz,
                              @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc,
                              @Binding.Args Object[] args, @Binding.Return Object returnObj) {
        if ("doCreateBean".equals(methodName)) {
            String createdBean = SpringBeanCreateTimeHolder.creatingBeanTrace.pop();

            //出栈
            Stack<InvokeTrace> invokeTraces = SpringBeanCreateTimeHolder.creatingBeanMap.get(createdBean);

            InvokeTrace invokeTrace = invokeTraces.pop();
            invokeTrace.stop = System.currentTimeMillis();
            invokeTrace.cost = invokeTrace.stop - invokeTrace.start;

            SpringBeanCreateTimeHolder.createdBeanMap.add(createdBean, invokeTrace);

            //Bean创建成功
            if (invokeTraces.empty()) {
                System.out.println("Bean初始化完成: " + createdBean);
                SpringBeanCreateTimeHolder.creatingBeanMap.remove(invokeTrace.beanName);
            }

            //Bean创建成功
            Root creatingRoot = SpringBeanCreateTimeHolder.creatingRoot;
            //Bean出栈
            creatingRoot.deepDown();
            //RootBean创建完成
            if (creatingRoot.head.trace.beanName.equals(createdBean) && creatingRoot.currentIsDequeue()) {

                SpringBeanCreateTimeHolder.createdRoots.offer(creatingRoot);
                SpringBeanCreateTimeHolder.creatingRoot = null;
                creatingRoot.head.setRoot(true);
                System.out.println("Root初始化完成: " + createdBean);
            }

        }
    }

}
