package com.designer.docker.service.impl;

import com.designer.docker.feign.Test1Feign;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class SmartInitializingSingletonServiceImpl implements InitializingBean, SmartInitializingSingleton {

    @Autowired
    private Service1Impl service1Impl;

    @Autowired
    private Service2Impl service2Impl;

    @Autowired
    private Service3Impl service3Impl;

    @Autowired
    private Service4Impl service4Impl;

    @Autowired
    private Test1Feign test4Feign;

    @Override
    public void afterSingletonsInstantiated() {
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 789) {
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(1000);
    }

}
