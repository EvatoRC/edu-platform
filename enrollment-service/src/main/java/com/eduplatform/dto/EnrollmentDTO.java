package com.eduplatform.dto;

import com.eduplatform.model.Enrollment.EnrollmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EnrollmentDTO {

    // ---- Request ----
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        @NotNull(message = "El ID del estudiante es obligatorio")
        private Long studentId;

        @NotEmpty(message = "Debe seleccionar al menos un curso")
        private List<Long> courseIds;

        private String discountCode;
    }

    // ---- Internal summary item ----
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CourseItemSummary {
        private Long courseId;
        private String courseName;
        private String instructor;
        private Integer durationHours;
        private BigDecimal unitPrice;
    }

    // ---- Main response / receipt ----
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long enrollmentId;
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private List<CourseItemSummary> courses;
        private BigDecimal subtotal;
        private BigDecimal discountPercent;
        private BigDecimal discountAmount;
        private BigDecimal totalToPay;
        private String discountCode;
        private EnrollmentStatus status;
        private LocalDateTime enrollmentDate;
        private String receiptMessage;
    }

    // ---- External API response wrappers ----
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StudentServiceResponse {
        private boolean success;
        private StudentData data;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class StudentData {
            private Long id;
            private String name;
            private String email;
            private Boolean active;
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CourseServiceResponse {
        private boolean success;
        private CourseData data;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor
        public static class CourseData {
            private Long id;
            private String name;
            private String instructor;
            private Integer durationHours;
            private java.math.BigDecimal cost;
            private Boolean available;
        }
    }
}
