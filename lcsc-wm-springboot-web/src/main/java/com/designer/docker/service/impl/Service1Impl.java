package com.designer.docker.service.impl;

import com.designer.docker.feign.Test1Feign;
import com.designer.docker.service.TestService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class Service1Impl implements TestService, InitializingBean {

    @Autowired
    private Service1Impl service1Impl;

    @Autowired
    private Service3Impl service3Impl;

    @Autowired
    private SmartInitializingSingletonServiceImpl smartInitializingSingletonServiceImpl;

    @Autowired
    private Test1Feign test1Feign;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(130);
    }

}
