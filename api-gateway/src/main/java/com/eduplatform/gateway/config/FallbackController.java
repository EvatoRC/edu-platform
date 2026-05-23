package com.eduplatform.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class FallbackController {

    private ResponseEntity<Map<String, Object>> fallbackResponse(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "El servicio de " + service + " no está disponible temporalmente. Intente más tarde.",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @RequestMapping("/fallback/courses")
    public ResponseEntity<Map<String, Object>> coursesFallback() {
        return fallbackResponse("cursos");
    }

    @RequestMapping("/fallback/students")
    public ResponseEntity<Map<String, Object>> studentsFallback() {
        return fallbackResponse("estudiantes");
    }

    @RequestMapping("/fallback/enrollments")
    public ResponseEntity<Map<String, Object>> enrollmentsFallback() {
        return fallbackResponse("inscripciones");
    }
}
