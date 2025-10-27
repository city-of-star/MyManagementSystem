package com.mms.common.web.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【Swagger 配置】
 *
 * @author li.hongyu
 * @date 2025-10-27 23:21:44
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MMS管理系统API文档")
                        .description("MMS管理系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("MMS开发团队")
                                .email("2825646787@qq.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}