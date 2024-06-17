package com.designer.docker.web;

import com.lcsc.wm.agent.interceptor.SpringBeanCreateTimeHolder;
import com.lcsc.wm.agent.model.InvokeTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: Designer
 * @date : 2023-10-18 23:40
 */
@Slf4j
@RestController
public class IndexController implements ApplicationRunner {

    @RequestMapping("/test")
    public void export(HttpServletResponse httpServletResponse) {
        MultiValueMap<String, InvokeTrace> createdBeanMap = SpringBeanCreateTimeHolder.createdBeanMap;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.error("IndexController");
    }

}
