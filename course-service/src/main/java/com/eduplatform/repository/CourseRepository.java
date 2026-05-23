package com.eduplatform.repository;

import com.eduplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByAvailableTrue();

    List<Course> findByCategoryIgnoreCaseAndAvailableTrue(String category);

    List<Course> findByCostBetweenAndAvailableTrue(BigDecimal min, BigDecimal max);

    @Query("SELECT c FROM Course c WHERE c.available = true AND " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByKeyword(String keyword);

    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.available = true ORDER BY c.category")
    List<String> findDistinctCategories();

    boolean existsByNameIgnoreCase(String name);
}
