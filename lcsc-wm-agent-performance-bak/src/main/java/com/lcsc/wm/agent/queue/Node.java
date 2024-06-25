package com.lcsc.wm.agent.queue;

import com.lcsc.wm.agent.model.InvokeTrace;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:11
 */
@NoArgsConstructor
public class Node {

    public InvokeTrace trace;

    public Node pre;

    public Node next;

    @Getter
    @Setter
    private boolean isRoot;

    public Node(InvokeTrace trace) {
        this.trace = trace;
    }

    /**
     * 计算当前节点真实耗时
     */
    public void computeReelCost() {
        trace.realCost = pre.trace.stop - next.trace.start;
    }

}
