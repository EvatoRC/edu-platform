package com.eduplatform;

import com.eduplatform.dto.CourseDTO;
import com.eduplatform.exception.BusinessException;
import com.eduplatform.exception.ResourceNotFoundException;
import com.eduplatform.model.Course;
import com.eduplatform.repository.CourseRepository;
import com.eduplatform.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock private CourseRepository courseRepository;
    @InjectMocks private CourseService courseService;

    private Course sample;

    @BeforeEach
    void setUp() {
        sample = Course.builder().id(1L).name("Spring Boot Test")
                .instructor("Prof X").durationHours(30)
                .cost(BigDecimal.valueOf(99.99)).category("Backend")
                .available(true).createdAt(LocalDateTime.now()).build();
    }

    @Test
    void getAllAvailable_returnsList() {
        when(courseRepository.findByAvailableTrue()).thenReturn(List.of(sample));
        List<CourseDTO.Response> result = courseService.getAllAvailable();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Spring Boot Test");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> courseService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_duplicateName_throwsBusinessException() {
        when(courseRepository.existsByNameIgnoreCase("Spring Boot Test")).thenReturn(true);
        CourseDTO.CreateRequest req = CourseDTO.CreateRequest.builder()
                .name("Spring Boot Test").instructor("Alguien")
                .durationHours(10).cost(BigDecimal.TEN).category("Backend").build();
        assertThatThrownBy(() -> courseService.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un curso");
    }

    @Test
    void create_newCourse_savesAndReturns() {
        when(courseRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(sample);
        CourseDTO.CreateRequest req = CourseDTO.CreateRequest.builder()
                .name("Nuevo").instructor("Prof").durationHours(20)
                .cost(BigDecimal.valueOf(50)).category("Diseño").build();
        CourseDTO.Response result = courseService.create(req);
        assertThat(result).isNotNull();
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void toggleAvailability_available_setsUnavailable() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(courseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        CourseDTO.Response result = courseService.toggleAvailability(1L);
        assertThat(result.getAvailable()).isFalse();
    }
}
