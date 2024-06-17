package com.lcsc.wm.agent.queue;

import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:32
 */
@Data
public class Root {

    public int deep = 0;

    public Node head;

    public Node tail;

    @Override
    public String toString() {
        return head.trace.beanName;
    }

}
