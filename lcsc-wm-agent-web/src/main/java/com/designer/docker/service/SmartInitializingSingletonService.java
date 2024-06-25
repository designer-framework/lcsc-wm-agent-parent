package com.designer.docker.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class SmartInitializingSingletonService implements SmartInitializingSingleton, InitializingBean {

    @Override
    public void afterSingletonsInstantiated() {
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 3625) {
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(1000);
    }

}
