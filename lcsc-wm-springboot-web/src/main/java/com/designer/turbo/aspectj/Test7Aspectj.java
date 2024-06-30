package com.designer.turbo.aspectj;

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
@Aspect
@Component
public class Test7Aspectj {

    @Pointcut("(execution(* com..*.*(..)) || execution(* org..*.*(..))) && @annotation(com.designer.turbo.annotation.Test)")
    public void testPointcut() {
    }

    @Around("testPointcut()")
    Object proceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed();
    }

}
