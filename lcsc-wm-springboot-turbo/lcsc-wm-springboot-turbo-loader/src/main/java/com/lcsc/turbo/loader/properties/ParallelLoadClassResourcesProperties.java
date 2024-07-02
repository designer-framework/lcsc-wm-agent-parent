package com.lcsc.turbo.loader.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-03 00:16
 */
@Data
@ConfigurationProperties(prefix = "spring.turbo.loader")
public class ParallelLoadClassResourcesProperties {

    private boolean enabled = true;

    private List<String> scanPackages = Collections.singletonList("com.lcsc");

}
