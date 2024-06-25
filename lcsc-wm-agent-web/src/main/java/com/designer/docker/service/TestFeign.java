package com.designer.docker.service;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-26 00:26
 */
@FeignClient(name = "TestFeign", url = "http://localhost:7989")
public interface TestFeign {
}
