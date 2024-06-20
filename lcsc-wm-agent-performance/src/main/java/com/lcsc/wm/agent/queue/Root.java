package com.lcsc.wm.agent.queue;

import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:32
 */
@Data
public class Root {

    public Node head;
    public Node tail;
    private int deep = 0;
    private String beforeCreated;

    @Override
    public String toString() {
        return head.trace.beanName;
    }

    public long getCostTime() {
        return tail.trace.stop - head.trace.start;
    }

    public int deepUp() {
        return ++deep;
    }

    /**
     * 出栈
     */
    public void deepDown() {
        deep--;
    }

    public boolean currentIsDequeue() {
        return deep == 0;
    }

}
