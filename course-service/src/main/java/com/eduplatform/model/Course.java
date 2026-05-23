package com.eduplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "COURSES")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_seq")
    @SequenceGenerator(name = "course_seq", sequenceName = "COURSE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @NotBlank
    @Size(max = 150)
    @Column(name = "INSTRUCTOR", nullable = false, length = 150)
    private String instructor;

    @NotNull
    @Positive
    @Column(name = "DURATION_HOURS", nullable = false)
    private Integer durationHours;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "COST", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Size(max = 1000)
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @NotBlank
    @Size(max = 100)
    @Column(name = "CATEGORY", length = 100)
    private String category;

    @Column(name = "AVAILABLE", nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Positive
    @Column(name = "MAX_STUDENTS")
    private Integer maxStudents;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
