package com.payflowx.audit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI auditOpenApi() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("PayFlowX Audit Service API")
                                .version("v1")
                                .description("Audit APIs"));
    }
}