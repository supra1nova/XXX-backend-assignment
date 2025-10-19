package com.assignment.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${app.title}")
    private String title;

    @Value("${app.description}")
    private String description;

    @Value("${app.version}")
    private String version;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title(title)
                .description(description)
                .version(version));
    }
}
