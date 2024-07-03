package com.designer.turbo.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-01 00:50
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
}
