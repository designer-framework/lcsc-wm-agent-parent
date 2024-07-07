package com.designer.turbo;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableApolloConfig
@EnableFeignClients
@EnableDiscoveryClient(autoRegister = false)
@SpringBootApplication(scanBasePackages = {"com.designer", "com.lcsc"})
public class TestClassLoaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestClassLoaderApplication.class, args);
    }

}
