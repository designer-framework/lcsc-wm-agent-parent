package com.designer.turbo.service.impl;

import com.designer.turbo.feign.Test3Feign;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class Service3Impl implements InitializingBean {

    @Autowired
    private Service1Impl service1Impl;

    @Autowired
    private Test3Feign test3Feign;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(190);
    }

}
