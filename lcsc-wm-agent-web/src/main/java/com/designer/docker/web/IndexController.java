package com.designer.docker.web;

import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeHolder;
import com.lcsc.wm.agent.queue.Node;
import com.lcsc.wm.agent.queue.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @author: Designer
 * @date : 2023-10-18 23:40
 */
@Slf4j
@RestController
public class IndexController implements ApplicationRunner {

    @RequestMapping("/test")
    public void export(HttpServletResponse httpServletResponse) {
        LinkedBlockingQueue<Root> createdRoots = SpringBeanCreateTimeHolder.createdRoots;

        createdRoots.parallelStream().forEach(root -> {

            Node head = root.head;
            Node tail = root.tail;

            if (head == tail) {

                //无依赖对象
                root.head.trace.realCost = head.trace.start - tail.trace.stop;
                return;

            } else {

                //
                head.trace.realCost = (tail.trace.stop - tail.next.trace.start) + (tail.trace.start - tail.pre.trace.stop);

                Node tempHead;
                while (true) {
                    if ((tempHead = head.next) != null) {
                        root.head.trace.realCost = head.trace.start - tail.trace.stop;
                    }
                    root.head.computeReelCost();
                }
            }

        });

        Root[] array = createdRoots.toArray(new Root[]{});
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("IndexController");
    }

}
