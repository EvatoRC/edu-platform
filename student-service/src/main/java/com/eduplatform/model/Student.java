package com.eduplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "STUDENTS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
    @SequenceGenerator(name = "student_seq", sequenceName = "STUDENT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @NotBlank
    @Email
    @Column(name = "EMAIL", nullable = false, unique = true, length = 200)
    private String email;

    @Size(max = 20)
    @Column(name = "PHONE", length = 20)
    private String phone;

    @Size(max = 500)
    @Column(name = "BIO", length = 500)
    private String bio;

    @Column(name = "ACTIVE", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
