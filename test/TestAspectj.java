package com.designer.turbo.test.tests.aspectj;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
    Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        log.error("{}.{}", joinPoint.getTarget().getClass(), ((MethodSignature) joinPoint.getSignature()).getMethod().getName());
        return joinPoint.proceed();
    }

}
