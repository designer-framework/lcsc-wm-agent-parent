package com.designer.turbo.configuration;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-29 13:39
 */
//@Configuration
@EnableSwagger2
public class Swagger2AutoConfiguration {

    @Bean
    public Docket createRestApi() {
        //
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("测试项目API")
                .description("测试项目API管理")
                .termsOfServiceUrl("https://github.com/designer-framework/lcsc-wm-agent-parent")
                .version("1.0")
                .build();
        //
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Agent系统")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.designer"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 用于定义API主界面的信息，比如可以声明所有的API的总标题、描述、版本
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("测试项目API")
                .description("测试项目API管理")
                .termsOfServiceUrl("https://github.com/designer-framework/lcsc-wm-agent-parent")
                .version("1.0")
                .build();
    }
}
