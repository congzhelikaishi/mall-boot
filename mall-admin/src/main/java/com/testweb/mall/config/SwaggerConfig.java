package com.testweb.mall.config;

import com.testweb.mall.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * API文档显示页面
 */
@Configuration
@EnableSwagger2 // 开启Swagger
public class SwaggerConfig extends BaseSwaggerConfig {
    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.testweb.mall.controller") // 扫描指定包的接口
                .title("mall后台系统")
                .description("mall后台相关接口文档")
                .contactName("testweb")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
