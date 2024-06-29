package com.designer.docker.service.impl;

import com.designer.docker.service.TestService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 23:02
 */
@Service
public class PostConstructServiceImpl implements TestService {

    @SneakyThrows
    @PostConstruct
    public void afterSingletonsInstantiated() {
        Thread.sleep(666);
    }

}
