package com.chuwa.cartservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shopping Platform Emart API - Cart Service")
                        .version("1.0")
                        .description("Handles shopping cart operations. Users can add, remove, clear and view cart items. "
                                + "Supports guest and logged-in user carts, stored in Redis for performance."))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) //  Enable Security Globally
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));


    }
}
