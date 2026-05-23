package com.eduplatform;

import com.eduplatform.dto.StudentDTO;
import com.eduplatform.exception.BusinessException;
import com.eduplatform.exception.ResourceNotFoundException;
import com.eduplatform.model.Student;
import com.eduplatform.repository.StudentRepository;
import com.eduplatform.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @InjectMocks private StudentService studentService;

    private Student sample;

    @BeforeEach
    void setUp() {
        sample = Student.builder().id(1L).name("María López")
                .email("maria@test.com").phone("+56912345678")
                .active(true).createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now()).build();
    }

    @Test
    void getAll_returnsActiveStudents() {
        when(studentRepository.findByActiveTrue()).thenReturn(List.of(sample));
        List<StudentDTO.Response> result = studentService.getAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("maria@test.com");
    }

    @Test
    void getById_notFound_throwsException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> studentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_duplicateEmail_throwsBusinessException() {
        when(studentRepository.existsByEmailIgnoreCase("maria@test.com")).thenReturn(true);
        StudentDTO.CreateRequest req = StudentDTO.CreateRequest.builder()
                .name("Otro").email("maria@test.com").build();
        assertThatThrownBy(() -> studentService.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe un estudiante");
    }

    @Test
    void create_newStudent_savesAndReturns() {
        when(studentRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(sample);
        StudentDTO.CreateRequest req = StudentDTO.CreateRequest.builder()
                .name("María López").email("maria@test.com").build();
        StudentDTO.Response result = studentService.create(req);
        assertThat(result).isNotNull();
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void deactivate_activeStudent_setsInactive() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StudentDTO.Response result = studentService.deactivate(1L);
        assertThat(result.getActive()).isFalse();
    }
}
