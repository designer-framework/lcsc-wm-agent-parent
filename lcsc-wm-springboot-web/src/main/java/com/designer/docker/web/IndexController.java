/*
package com.designer.docker.web;

import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeHolder;
import com.lcsc.wm.agent.model.InvokeTrace;
import com.lcsc.wm.agent.queue.Node;
import com.lcsc.wm.agent.queue.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

*/
/**
 * @description:
 * @author: Designer
 * @date : 2023-10-18 23:40
 *//*

@Slf4j
@RestController
public class IndexController implements ApplicationRunner {

    @RequestMapping("/sort")
    public void sort() {
        List<Root> sortedRoots = SpringBeanCreateTimeHolder.createdRoots.stream()
                .map(this::calcReelCostTime)
                .sorted(Comparator.comparingLong(Root::getCostTime).reversed())
                .collect(Collectors.toList());

        StringBuffer treeBuffer = new StringBuffer();
        sortedRoots.stream()
                .map(this::calcReelCostTimeTree)
                .forEach(treeBuffer::append);
        log.error("TraceTree: \n{}", treeBuffer);
    }

    private Root calcReelCostTime(Root root) {
        StringBuffer chain = new StringBuffer();
        Node head = root.head;
        Node tail = root.tail;
        //简单Bean
        if (head == tail) {
            InvokeTrace invokeTrace = root.tail.trace;
            invokeTrace.setRealCost(root.tail.trace.cost);

            //复杂Bean
        } else {
            InvokeTrace tailInvokeTrace = tail.trace;
            tailInvokeTrace.setRealCost(tailInvokeTrace.stop - tailInvokeTrace.start);

            //累计耗时
            long sumReelCostTime = tailInvokeTrace.getCost();

            Node currNode = tail;
            while ((currNode = currNode.pre) != null) {

                //
                InvokeTrace currInvokeTrace = currNode.trace;
                //当前Bean加载真实耗时
                currInvokeTrace.setRealCost(currInvokeTrace.cost - sumReelCostTime);
                sumReelCostTime += currInvokeTrace.getRealCost();

                if (currNode == currNode.pre) {
                    break;
                }

            }

        }

        root.setTraceString(chain.toString());
        return root;
    }

    private String calcReelCostTimeTree(Root root) {
        StringBuilder chain = new StringBuilder();
        Node head = root.head;
        Node tail = root.tail;

        //简单Bean
        if (head == tail) {
            InvokeTrace tailTrace = root.tail.trace;


            chain.append("|-->");
            chain.append(tailTrace.beanName);
            chain.append("耗时: ");
            chain.append(tailTrace.realCost);
            chain.append("/ms");
            chain.append("\n");

            //复杂Bean
        } else {

            Node headNode = head;
            chain.append("|-->");
            chain.append(headNode.trace.beanName);
            chain.append("耗时: ");
            chain.append(headNode.trace.realCost);
            chain.append("/ms");
            chain.append("\n");
            while ((headNode = headNode.next) != null) {

                InvokeTrace headTrace = headNode.trace;
                chain.append("|");
                for (int i = 0; i < headTrace.deep; i++) {
                    chain.append(" ").append(" ").append(" ").append(" ");
                }
                chain.append("> ");
                chain.append(headTrace.beanName);
                chain.append("耗时: ");
                chain.append(headTrace.realCost);
                chain.append("/ms");
                chain.append("\n");

            }

        }

        return chain.toString();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("IndexController");
    }

}
*/
