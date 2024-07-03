package com.designer.turbo.service.impl;

import com.designer.turbo.feign.Test4Feign;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class Service4Impl implements InitializingBean {

    @Autowired
    private Service1Impl service1Impl;

    @Autowired
    private Service2Impl service2Impl;

    @Autowired
    private Service3Impl service3Impl;

    @Autowired
    private Service4Impl service4Impl;

    @Autowired
    private Test4Feign test4Feign;

    @Autowired
    private SmartInitializingSingletonServiceImpl smartInitializingSingletonServiceImpl;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(220);
    }

}
