package com.designer.turbo.test.tests.service;

import com.designer.turbo.annotation.Test;
import com.designer.turbo.service.BaseService;
import com.designer.turbo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Template${index}ServiceImpl extends BaseService implements TestService, InitializingBean {
    ${method}

    @Test
    @Override
    public void afterPropertiesSet() throws Exception {
        //Thread.sleep(1);
    }

}
