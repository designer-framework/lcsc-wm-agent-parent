package com.lcsc.wm.agent.queue;

import com.lcsc.wm.agent.model.InvokeTrace;
import lombok.NoArgsConstructor;

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

    public Node(InvokeTrace trace) {
        this.trace = trace;
    }

}
