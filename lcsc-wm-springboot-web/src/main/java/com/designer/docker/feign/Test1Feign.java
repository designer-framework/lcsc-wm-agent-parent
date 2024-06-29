package com.designer.docker.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-26 00:26
 */
@FeignClient(contextId = "agent-Test1Feign", name = "application-consumer", path = "/agent/api/v1")
public interface Test1Feign extends TestFeign {
}
