package com.eduplatform.service;

import com.eduplatform.dto.StudentDTO;
import com.eduplatform.exception.BusinessException;
import com.eduplatform.exception.ResourceNotFoundException;
import com.eduplatform.model.Student;
import com.eduplatform.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<StudentDTO.Response> getAll() {
        return studentRepository.findByActiveTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudentDTO.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public StudentDTO.Response getByEmail(String email) {
        Student s = studentRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con email: " + email));
        return toResponse(s);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO.Response> searchByName(String name) {
        return studentRepository.searchByName(name).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public StudentDTO.Response create(StudentDTO.CreateRequest req) {
        if (studentRepository.existsByEmailIgnoreCase(req.getEmail())) {
            throw new BusinessException("Ya existe un estudiante registrado con el email: " + req.getEmail());
        }
        Student student = Student.builder()
                .name(req.getName()).email(req.getEmail())
                .phone(req.getPhone()).bio(req.getBio())
                .active(true).build();
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDTO.Response update(Long id, StudentDTO.UpdateRequest req) {
        Student student = findOrThrow(id);
        if (req.getName() != null && !req.getName().isBlank()) student.setName(req.getName());
        if (req.getPhone() != null) student.setPhone(req.getPhone());
        if (req.getBio() != null) student.setBio(req.getBio());
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentDTO.Response deactivate(Long id) {
        Student student = findOrThrow(id);
        student.setActive(false);
        return toResponse(studentRepository.save(student));
    }

    private Student findOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    private StudentDTO.Response toResponse(Student s) {
        return StudentDTO.Response.builder()
                .id(s.getId()).name(s.getName()).email(s.getEmail())
                .phone(s.getPhone()).bio(s.getBio()).active(s.getActive())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
