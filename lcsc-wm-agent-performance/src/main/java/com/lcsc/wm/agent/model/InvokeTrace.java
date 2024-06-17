package com.lcsc.wm.agent.model;

import lombok.Data;
import lombok.experimental.Accessors;
import sun.misc.Contended;

import java.text.MessageFormat;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 19:31
 */
@Data
@Contended
@Accessors(chain = true)
public class InvokeTrace {

    public String beanName;

    public long start;

    public long stop;

    public long cost;

    @Override
    public String toString() {
        return MessageFormat.format("Bean{0}加载完毕, 耗时: {1}", beanName, cost);
    }
}
