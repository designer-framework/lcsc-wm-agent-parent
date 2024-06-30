package com.designer.turbo.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-26 00:26
 */
@FeignClient(contextId = "agent-Test3Feign", name = "application-consumer", path = "/agent/api/v3")
public interface Test3Feign extends TestFeign {
}
