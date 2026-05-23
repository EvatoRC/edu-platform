package com.eduplatform.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotBlank(message = "El nombre del curso es obligatorio")
        @Size(max = 200)
        private String name;

        @NotBlank(message = "El instructor es obligatorio")
        @Size(max = 150)
        private String instructor;

        @NotNull(message = "La duración en horas es obligatoria")
        @Positive(message = "La duración debe ser mayor a cero")
        private Integer durationHours;

        @NotNull(message = "El costo es obligatorio")
        @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
        private BigDecimal cost;

        @Size(max = 1000)
        private String description;

        @NotBlank(message = "La categoría es obligatoria")
        private String category;

        @Positive
        private Integer maxStudents;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String instructor;
        private Integer durationHours;
        private BigDecimal cost;
        private String description;
        private String category;
        private Boolean available;
        private Integer maxStudents;
        private LocalDateTime createdAt;
    }
}
