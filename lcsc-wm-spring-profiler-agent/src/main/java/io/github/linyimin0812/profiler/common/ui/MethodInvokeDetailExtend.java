package io.github.linyimin0812.profiler.common.ui;

import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 **/
@Getter
public class MethodInvokeDetailExtend extends MethodInvokeDetail {

    public MethodInvokeDetailExtend(String methodQualifier, Object[] args) {
        super(methodQualifier, args);
    }

}
