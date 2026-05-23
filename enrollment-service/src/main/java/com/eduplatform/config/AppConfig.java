package com.eduplatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Enrollment Service API").version("1.0.0")
                .description("Gestión de inscripciones y generación de boletas"));
    }

    @Bean("courseWebClient")
    public WebClient courseWebClient(@Value("${services.course-service.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean("studentWebClient")
    public WebClient studentWebClient(@Value("${services.student-service.url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
