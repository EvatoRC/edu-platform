package com.eduplatform.controller;

import com.eduplatform.dto.ApiResponse;
import com.eduplatform.dto.EnrollmentDTO;
import com.eduplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Gestión de inscripciones y boletas")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Inscribir estudiante en uno o más cursos",
               description = "Genera boleta con subtotal, descuentos y total a pagar")
    public ResponseEntity<ApiResponse<EnrollmentDTO.Response>> create(
            @Valid @RequestBody EnrollmentDTO.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(enrollmentService.createEnrollment(req), "Inscripción realizada exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripción por ID")
    public ResponseEntity<ApiResponse<EnrollmentDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(enrollmentService.getById(id), "Inscripción encontrada"));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Historial de inscripciones por ID de estudiante")
    public ResponseEntity<ApiResponse<List<EnrollmentDTO.Response>>> getByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(enrollmentService.getByStudentId(studentId), "Historial de inscripciones"));
    }

    @GetMapping("/student/email")
    @Operation(summary = "Historial de inscripciones por email de estudiante")
    public ResponseEntity<ApiResponse<List<EnrollmentDTO.Response>>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(enrollmentService.getByStudentEmail(email), "Historial de inscripciones"));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancelar una inscripción")
    public ResponseEntity<ApiResponse<EnrollmentDTO.Response>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(enrollmentService.cancel(id), "Inscripción cancelada"));
    }
}
