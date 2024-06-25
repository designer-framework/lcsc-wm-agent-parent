package com.designer.docker.service;

import lombok.SneakyThrows;
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

    @SneakyThrows
    @Override
    public void afterSingletonsInstantiated() {
        Thread.sleep(3000);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(1000);
    }

}
