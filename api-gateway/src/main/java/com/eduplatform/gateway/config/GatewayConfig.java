package com.eduplatform.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${services.course-service.url}")
    private String courseServiceUrl;

    @Value("${services.student-service.url}")
    private String studentServiceUrl;

    @Value("${services.enrollment-service.url}")
    private String enrollmentServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ── Course Service ──────────────────────────────────────────
                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "edu-platform-gateway")
                                .circuitBreaker(c -> c
                                        .setName("courseServiceCB")
                                        .setFallbackUri("forward:/fallback/courses")))
                        .uri(courseServiceUrl))

                // ── Student Service ─────────────────────────────────────────
                .route("student-service", r -> r
                        .path("/api/students/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "edu-platform-gateway")
                                .circuitBreaker(c -> c
                                        .setName("studentServiceCB")
                                        .setFallbackUri("forward:/fallback/students")))
                        .uri(studentServiceUrl))

                // ── Enrollment Service ──────────────────────────────────────
                .route("enrollment-service", r -> r
                        .path("/api/enrollments/**")
                        .filters(f -> f
                                .addRequestHeader("X-Gateway", "edu-platform-gateway")
                                .circuitBreaker(c -> c
                                        .setName("enrollmentServiceCB")
                                        .setFallbackUri("forward:/fallback/enrollments")))
                        .uri(enrollmentServiceUrl))

                .build();
    }
}
