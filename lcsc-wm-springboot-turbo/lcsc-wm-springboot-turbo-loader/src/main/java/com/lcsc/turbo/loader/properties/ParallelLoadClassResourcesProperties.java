package com.lcsc.turbo.loader.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-03 00:16
 */
@Data
@ConfigurationProperties(prefix = "spring.turbo.loader")
public class ParallelLoadClassResourcesProperties {

    private boolean enabled = true;

}
