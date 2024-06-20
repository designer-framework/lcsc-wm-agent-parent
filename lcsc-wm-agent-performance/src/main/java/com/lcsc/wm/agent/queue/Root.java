package com.lcsc.wm.agent.queue;

import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:32
 */
@Data
public class Root {

    public int level = 0;
    public Node head;
    public Node tail;
    private boolean beforeIsEnqueue = true;
    private int deep = 0;

    @Override
    public String toString() {
        return head.trace.beanName;
    }

    public long getCostTime() {
        return tail.trace.stop - head.trace.start;
    }

    public void levelUp() {
        level++;
    }

    public void levelDown() {
        level--;
    }

    public void deepUp() {
        deep++;
    }

    /**
     * 出栈
     */
    public void deepDown() {
        deep--;
        if (deep == 0) {
            beforeIsEnqueue = false;
            levelDown();
        }
    }

    public boolean beforeIsEnqueue() {
        return beforeIsEnqueue;
    }

    public boolean currentIsDequeue() {
        return deep == 0;
    }

}
