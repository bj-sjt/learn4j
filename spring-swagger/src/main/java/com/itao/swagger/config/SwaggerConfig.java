package com.itao.swagger.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger3 与 spring boot 2.6.* 整合会报错， 降低 spring boot 版本即可
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket devDocket(Environment env) {

        // 设置要暴漏接口文档的配置环境
        Profiles profile = Profiles.of("dev", "dev");
        boolean flag = env.acceptsProfiles(profile);
        return new Docket(DocumentationType.OAS_30)
                .groupName("dev")
                .apiInfo(apiInfo())
                .enable(flag)
                .select()
                //显示这个包下的所有文档
                .apis(RequestHandlerSelectors.basePackage("com.itao.swagger.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket testDocket(Environment env) {

        // 设置要暴漏接口文档的配置环境
        Profiles profile = Profiles.of("test", "test");
        boolean flag = env.acceptsProfiles(profile);
        return new Docket(DocumentationType.OAS_30)
                .groupName("test")
                .apiInfo(apiInfo())
                .enable(flag)
                .select()
                //显示方法上有 @ApiOperation 注解的所有文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger3-Demo接口文档")
                .description("技术支持-×××××技术团队")
                .contact(new Contact("×××××技术团队", "https://www.itao.cloud", "320885976@qq.com "))
                .version("1.0")
                .build();
    }
}
