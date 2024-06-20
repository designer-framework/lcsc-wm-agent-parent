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

/**
 * @description:
 * @author: Designer
 * @date : 2023-10-18 23:40
 */
@Slf4j
@RestController
public class IndexController implements ApplicationRunner {

    @RequestMapping("/sort")
    public void sort() {
        List<Root> sortedRoots = SpringBeanCreateTimeHolder.createdRoots.stream()
                .map(this::calcReelCostTime)
                .sorted(Comparator.comparingLong(Root::getCostTime).reversed())
                .collect(Collectors.toList());

    }

    private Root calcReelCostTime(Root root) {
        Node head = root.head;
        Node tail = root.tail;
        //简单Bean
        if (head == tail) {
            InvokeTrace invokeTrace = root.tail.trace;
            invokeTrace.setRealCost(root.tail.trace.cost);

            //复杂Bean
        } else {
            InvokeTrace invokeTrace = root.tail.trace;
            invokeTrace.setRealCost(root.tail.trace.cost);

            //累计耗时
            long sumReelCostTime = invokeTrace.getRealCost();
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
        return root;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("IndexController");
    }

}
