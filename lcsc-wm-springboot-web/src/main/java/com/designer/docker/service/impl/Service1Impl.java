package com.designer.docker.service.impl;

import com.designer.docker.feign.TestFeign;
import com.designer.docker.service.TestService;
import com.designer.docker.vo.Test2VO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Slf4j
@Service
public class Service1Impl implements TestService, InitializingBean {

    @Autowired
    private Service1Impl service1Impl;

    @Autowired
    private Service3Impl service3Impl;

    @Autowired
    private SmartInitializingSingletonServiceImpl smartInitializingSingletonServiceImpl;

    @Autowired
    private Map<String, TestFeign> testFeignMap;

    @Override
    public void test() {
        testFeignMap.forEach((s, testFeign) -> {
            log.error(s, testFeign.api_2(new Test2VO()));
        });
    }

    public void test2() {
        System.out.println("2");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(130);
    }

}
