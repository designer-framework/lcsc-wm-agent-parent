package com.designer.turbo.service.impl.tests;

import com.designer.turbo.annotation.Test;
import com.designer.turbo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Template${index}ServiceImpl implements TestService, InitializingBean {
    ${method}

    @Test
    @Override
    public void afterPropertiesSet() throws Exception {
        //Thread.sleep(1);
    }

}
