package io.github.linyimin0812.profiler.common.ui;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-25 21:06
 **/
public class MethodInvokeDetailExtend extends MethodInvokeDetail {

    private final String invokeCountMethodAlias;

    public MethodInvokeDetailExtend(String methodQualifier, Object[] args, String invokeCountMethodAlias) {
        super(methodQualifier, args);
        this.invokeCountMethodAlias = invokeCountMethodAlias;
    }

}
