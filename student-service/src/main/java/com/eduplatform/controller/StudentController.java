package com.eduplatform.controller;

import com.eduplatform.dto.ApiResponse;
import com.eduplatform.dto.StudentDTO;
import com.eduplatform.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Gestión de estudiantes")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Listar todos los estudiantes activos")
    public ResponseEntity<ApiResponse<List<StudentDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getAll(), "Estudiantes obtenidos"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estudiante por ID")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getById(id), "Estudiante encontrado"));
    }

    @GetMapping("/email")
    @Operation(summary = "Obtener estudiante por email")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getByEmail(email), "Estudiante encontrado"));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar estudiantes por nombre")
    public ResponseEntity<ApiResponse<List<StudentDTO.Response>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.searchByName(name), "Resultados de búsqueda"));
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo estudiante")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> create(@Valid @RequestBody StudentDTO.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(studentService.create(req), "Estudiante registrado exitosamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un estudiante")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> update(
            @PathVariable Long id, @Valid @RequestBody StudentDTO.UpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.update(id, req), "Estudiante actualizado"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar un estudiante")
    public ResponseEntity<ApiResponse<StudentDTO.Response>> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.deactivate(id), "Estudiante desactivado"));
    }
}
