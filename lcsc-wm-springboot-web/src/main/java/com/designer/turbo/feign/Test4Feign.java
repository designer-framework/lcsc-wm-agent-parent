package com.designer.turbo.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-26 00:26
 */
@FeignClient(contextId = "agent-Test4Feign", name = "application-consumer", path = "/agent/api/v4")
public interface Test4Feign extends TestFeign {
}
