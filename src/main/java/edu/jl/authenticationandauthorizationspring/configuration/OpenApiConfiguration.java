package edu.jl.authenticationandauthorizationspring.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Authentication and Authorization API")
                .version("1.0.0")
                .description("API for managing authentication and authorization")
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/license/mit")));
    }
}
