package com.designer.turbo.test.tests.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-01 00:44
 */
@Slf4j
@Aspect
@Component
public class Test${index}Aspectj {

    ${method}
    public void testPointcut() {
    }

    @Around("testPointcut()")
    Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.error(getClass().toString());
        return proceedingJoinPoint.proceed();
    }

}
