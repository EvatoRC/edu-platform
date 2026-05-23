package com.eduplatform.service;

import com.eduplatform.dto.CourseDTO;
import com.eduplatform.exception.BusinessException;
import com.eduplatform.exception.ResourceNotFoundException;
import com.eduplatform.model.Course;
import com.eduplatform.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseDTO.Response> getAllAvailable() {
        return courseRepository.findByAvailableTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CourseDTO.Response> getByCategory(String category) {
        return courseRepository.findByCategoryIgnoreCaseAndAvailableTrue(category).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO.Response> search(String keyword) {
        return courseRepository.searchByKeyword(keyword).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO.Response> getByPriceRange(BigDecimal min, BigDecimal max) {
        return courseRepository.findByCostBetweenAndAvailableTrue(min, max).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return courseRepository.findDistinctCategories();
    }

    @Transactional
    public CourseDTO.Response create(CourseDTO.CreateRequest req) {
        if (courseRepository.existsByNameIgnoreCase(req.getName())) {
            throw new BusinessException("Ya existe un curso con el nombre: " + req.getName());
        }
        Course course = Course.builder()
                .name(req.getName()).instructor(req.getInstructor())
                .durationHours(req.getDurationHours()).cost(req.getCost())
                .description(req.getDescription()).category(req.getCategory())
                .maxStudents(req.getMaxStudents()).available(true)
                .build();
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseDTO.Response toggleAvailability(Long id) {
        Course course = findOrThrow(id);
        course.setAvailable(!course.getAvailable());
        return toResponse(courseRepository.save(course));
    }

    public Course findOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
    }

    private CourseDTO.Response toResponse(Course c) {
        return CourseDTO.Response.builder()
                .id(c.getId()).name(c.getName()).instructor(c.getInstructor())
                .durationHours(c.getDurationHours()).cost(c.getCost())
                .description(c.getDescription()).category(c.getCategory())
                .available(c.getAvailable()).maxStudents(c.getMaxStudents())
                .createdAt(c.getCreatedAt()).build();
    }
}
