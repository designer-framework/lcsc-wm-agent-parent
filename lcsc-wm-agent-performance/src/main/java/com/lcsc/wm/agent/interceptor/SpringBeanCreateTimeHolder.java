package com.lcsc.wm.agent.interceptor;

import com.lcsc.wm.agent.model.InvokeTrace;
import com.lcsc.wm.agent.queue.Root;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

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
    public static final LinkedHashMap<String, Stack<InvokeTrace>> creatingBeanMap = new LinkedHashMap<>();

    /**
     *
     */
    public static final MultiValueMap<String, InvokeTrace> createdBeanMap = new LinkedMultiValueMap<>();


    /**
     *
     */
    public static Root creatingRoot;

    /**
     *
     */
    public static LinkedBlockingQueue<Root> createdRoots = new LinkedBlockingQueue<>();

}
