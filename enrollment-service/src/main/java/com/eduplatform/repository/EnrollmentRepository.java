package com.eduplatform.repository;

import com.eduplatform.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentIdOrderByEnrollmentDateDesc(Long studentId);
    List<Enrollment> findByStudentEmailIgnoreCaseOrderByEnrollmentDateDesc(String email);
}
