package com.eduplatform.service;

import com.eduplatform.dto.EnrollmentDTO;
import com.eduplatform.dto.EnrollmentDTO.*;
import com.eduplatform.exception.BusinessException;
import com.eduplatform.exception.ResourceNotFoundException;
import com.eduplatform.model.Enrollment;
import com.eduplatform.model.Enrollment.EnrollmentStatus;
import com.eduplatform.model.EnrollmentItem;
import com.eduplatform.repository.EnrollmentItemRepository;
import com.eduplatform.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentItemRepository enrollmentItemRepository;
    private final DiscountService discountService;

    @Qualifier("courseWebClient")
    private final WebClient courseWebClient;

    @Qualifier("studentWebClient")
    private final WebClient studentWebClient;

    @Transactional
    public EnrollmentDTO.Response createEnrollment(CreateRequest req) {
        // Validar código de descuento
        if (!discountService.isValidCode(req.getDiscountCode())) {
            throw new BusinessException("Código de descuento inválido: " + req.getDiscountCode());
        }

        // Funcionalidad extra #3: verificar duplicados en la misma solicitud
        long distinct = req.getCourseIds().stream().distinct().count();
        if (distinct < req.getCourseIds().size()) {
            throw new BusinessException("No puedes incluir el mismo curso más de una vez en la misma inscripción.");
        }

        // Consultar student-service
        StudentServiceResponse.StudentData student = fetchStudent(req.getStudentId());
        if (!student.getActive()) {
            throw new BusinessException("El estudiante con ID " + req.getStudentId() + " está inactivo.");
        }

        // Consultar course-service para cada curso
        List<CourseServiceResponse.CourseData> courses = new ArrayList<>();
        for (Long courseId : req.getCourseIds()) {
            CourseServiceResponse.CourseData course = fetchCourse(courseId);
            if (!course.getAvailable()) {
                throw new BusinessException("El curso '" + course.getName() + "' no está disponible.");
            }
            courses.add(course);
        }

        // Calcular totales
        BigDecimal subtotal = courses.stream()
                .map(CourseServiceResponse.CourseData::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPct = discountService.resolveDiscount(req.getDiscountCode(), courses.size());
        BigDecimal discountAmount = subtotal
                .multiply(discountPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalCost = subtotal.subtract(discountAmount);

        // Persistir inscripción
        Enrollment enrollment = Enrollment.builder()
                .studentId(student.getId()).studentName(student.getName())
                .studentEmail(student.getEmail()).subtotal(subtotal)
                .discountPercent(discountPct).discountAmount(discountAmount)
                .finalCost(finalCost).status(EnrollmentStatus.CONFIRMED)
                .discountCode(req.getDiscountCode())
                .build();
        enrollment = enrollmentRepository.save(enrollment);

        // Persistir items
        List<CourseItemSummary> summaries = new ArrayList<>();
        for (CourseServiceResponse.CourseData c : courses) {
            EnrollmentItem item = EnrollmentItem.builder()
                    .enrollment(enrollment).courseId(c.getId())
                    .courseName(c.getName()).instructor(c.getInstructor())
                    .durationHours(c.getDurationHours()).unitPrice(c.getCost())
                    .build();
            enrollmentItemRepository.save(item);
            summaries.add(CourseItemSummary.builder()
                    .courseId(c.getId()).courseName(c.getName())
                    .instructor(c.getInstructor()).durationHours(c.getDurationHours())
                    .unitPrice(c.getCost()).build());
        }

        return buildResponse(enrollment, summaries);
    }

    @Transactional(readOnly = true)
    public EnrollmentDTO.Response getById(Long id) {
        Enrollment e = findOrThrow(id);
        List<CourseItemSummary> summaries = itemsToSummaries(enrollmentItemRepository.findByEnrollmentId(id));
        return buildResponse(e, summaries);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO.Response> getByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentIdOrderByEnrollmentDateDesc(studentId).stream()
                .map(e -> buildResponse(e, itemsToSummaries(enrollmentItemRepository.findByEnrollmentId(e.getId()))))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO.Response> getByStudentEmail(String email) {
        return enrollmentRepository.findByStudentEmailIgnoreCaseOrderByEnrollmentDateDesc(email).stream()
                .map(e -> buildResponse(e, itemsToSummaries(enrollmentItemRepository.findByEnrollmentId(e.getId()))))
                .collect(Collectors.toList());
    }

    // Funcionalidad extra #4: cancelación de inscripción
    @Transactional
    public EnrollmentDTO.Response cancel(Long id) {
        Enrollment e = findOrThrow(id);
        if (e.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BusinessException("La inscripción ya está cancelada.");
        }
        e.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(e);
        List<CourseItemSummary> summaries = itemsToSummaries(enrollmentItemRepository.findByEnrollmentId(id));
        return buildResponse(e, summaries);
    }

    // ---- Helpers ----

    private StudentServiceResponse.StudentData fetchStudent(Long studentId) {
        return studentWebClient.get()
                .uri("/api/students/{id}", studentId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new ResourceNotFoundException("Estudiante no encontrado con ID: " + studentId)))
                .bodyToMono(StudentServiceResponse.class)
                .map(StudentServiceResponse::getData)
                .block();
    }

    private CourseServiceResponse.CourseData fetchCourse(Long courseId) {
        return courseWebClient.get()
                .uri("/api/courses/{id}", courseId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new ResourceNotFoundException("Curso no encontrado con ID: " + courseId)))
                .bodyToMono(CourseServiceResponse.class)
                .map(CourseServiceResponse::getData)
                .block();
    }

    private Enrollment findOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + id));
    }

    private List<CourseItemSummary> itemsToSummaries(List<EnrollmentItem> items) {
        return items.stream().map(i -> CourseItemSummary.builder()
                .courseId(i.getCourseId()).courseName(i.getCourseName())
                .instructor(i.getInstructor()).durationHours(i.getDurationHours())
                .unitPrice(i.getUnitPrice()).build())
                .collect(Collectors.toList());
    }

    private EnrollmentDTO.Response buildResponse(Enrollment e, List<CourseItemSummary> summaries) {
        String receipt = buildReceiptMessage(e.getStudentName(), summaries.size(),
                e.getDiscountPercent(), e.getFinalCost());
        return EnrollmentDTO.Response.builder()
                .enrollmentId(e.getId()).studentId(e.getStudentId())
                .studentName(e.getStudentName()).studentEmail(e.getStudentEmail())
                .courses(summaries).subtotal(e.getSubtotal())
                .discountPercent(e.getDiscountPercent()).discountAmount(e.getDiscountAmount())
                .totalToPay(e.getFinalCost()).discountCode(e.getDiscountCode())
                .status(e.getStatus()).enrollmentDate(e.getEnrollmentDate())
                .receiptMessage(receipt).build();
    }

    private String buildReceiptMessage(String name, int count, BigDecimal pct, BigDecimal total) {
        StringBuilder sb = new StringBuilder("¡Hola ").append(name).append("! ");
        sb.append("Inscripción en ").append(count).append(count == 1 ? " curso" : " cursos").append(" confirmada. ");
        if (pct.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Descuento aplicado: ").append(pct).append("%. ");
        }
        sb.append("Total a pagar: $").append(total).append(". ¡Mucho éxito!");
        return sb.toString();
    }
}
