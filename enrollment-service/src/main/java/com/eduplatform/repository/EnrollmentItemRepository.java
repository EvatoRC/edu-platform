package com.eduplatform.repository;

import com.eduplatform.model.EnrollmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentItemRepository extends JpaRepository<EnrollmentItem, Long> {
    List<EnrollmentItem> findByEnrollmentId(Long enrollmentId);
}
