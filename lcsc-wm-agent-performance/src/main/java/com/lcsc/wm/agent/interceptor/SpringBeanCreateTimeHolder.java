package com.lcsc.wm.agent.interceptor;

import com.lcsc.wm.agent.model.InvokeTrace;
import com.lcsc.wm.agent.queue.Root;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 19:12
 */
public class SpringBeanCreateTimeHolder {

    /**
     *
     */
    public static final Stack<String> creatingBeanTrace = new Stack<>();
    public static final Map<String, Stack<InvokeTrace>> creatingBeanMap = new HashMap<>();

    /**
     *
     */
    public static final MultiValueMap<String, InvokeTrace> createdBeanMap = new LinkedMultiValueMap<>();

    /**
     *
     */
    public static Root creatingTree;

}
