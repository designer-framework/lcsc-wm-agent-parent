package com.designer.docker.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class ServiceTwo02 implements InitializingBean {

    @Autowired
    private SmartInitializingSingletonService smartInitializingSingletonService;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(200);
    }

}
