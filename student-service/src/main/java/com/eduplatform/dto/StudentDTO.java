package com.eduplatform.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class StudentDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150)
        private String name;

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        private String email;

        @Size(max = 20)
        private String phone;

        @Size(max = 500)
        private String bio;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRequest {
        @Size(max = 150)
        private String name;

        @Size(max = 20)
        private String phone;

        @Size(max = 500)
        private String bio;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String bio;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
