package com.eduplatform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ENROLLMENTS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollment_seq")
    @SequenceGenerator(name = "enrollment_seq", sequenceName = "ENROLLMENT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    // ID externo — viene del student-service, no FK local
    @Column(name = "STUDENT_ID", nullable = false)
    private Long studentId;

    @Column(name = "STUDENT_NAME", nullable = false, length = 150)
    private String studentName;

    @Column(name = "STUDENT_EMAIL", nullable = false, length = 200)
    private String studentEmail;

    @Column(name = "ENROLLMENT_DATE", nullable = false)
    private LocalDateTime enrollmentDate;

    @Column(name = "SUBTOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "DISCOUNT_PERCENT", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_AMOUNT", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "FINAL_COST", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.CONFIRMED;

    @Column(name = "DISCOUNT_CODE", length = 50)
    private String discountCode;

    @PrePersist
    protected void onCreate() {
        this.enrollmentDate = LocalDateTime.now();
    }

    public enum EnrollmentStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
