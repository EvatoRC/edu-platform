package com.eduplatform.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ENROLLMENT_ITEMS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnrollmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enrollment_item_seq")
    @SequenceGenerator(name = "enrollment_item_seq", sequenceName = "ENROLLMENT_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ENROLLMENT_ID", nullable = false)
    private Enrollment enrollment;

    // IDs externos — vienen del course-service
    @Column(name = "COURSE_ID", nullable = false)
    private Long courseId;

    @Column(name = "COURSE_NAME", nullable = false, length = 200)
    private String courseName;

    @Column(name = "INSTRUCTOR", nullable = false, length = 150)
    private String instructor;

    @Column(name = "DURATION_HOURS", nullable = false)
    private Integer durationHours;

    @Column(name = "UNIT_PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
}
