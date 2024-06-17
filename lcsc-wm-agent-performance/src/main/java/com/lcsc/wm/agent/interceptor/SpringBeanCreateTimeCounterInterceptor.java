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

            Root treeRoot = SpringBeanCreateTimeHolder.creatingTree;
            //首次初始化
            if (treeRoot == null) {
                Root creatingTree = (SpringBeanCreateTimeHolder.creatingTree = new Root());

                Node preNode = new Node(invokeTrace);
                preNode.pre = preNode;

                Node nextNode = new Node();
                nextNode.pre = preNode;
                preNode.next = nextNode;

                creatingTree.head = preNode;
                creatingTree.tail = nextNode;

                creatingTree.deep++;

                //初始化尾节点
            } else {

                Node tailNode = treeRoot.tail;
                tailNode.trace = invokeTrace;

                Node nextNode = new Node();
                nextNode.pre = tailNode;
                tailNode.next = nextNode;
                treeRoot.tail = nextNode;

                treeRoot.deep++;

            }

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

            //Bean创建成功, 计算各bean的初始化时间
            int deep = SpringBeanCreateTimeHolder.creatingTree.deep - 1;
            if (SpringBeanCreateTimeHolder.creatingTree.head.trace.beanName.equals(createdBean) && deep == 0) {
                System.out.println("Root初始化完成: " + createdBean);
                SpringBeanCreateTimeHolder.creatingTree = null;
            } else {
                SpringBeanCreateTimeHolder.creatingTree.deep = deep;
            }

        }
    }

}
