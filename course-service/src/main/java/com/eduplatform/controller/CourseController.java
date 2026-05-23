package com.eduplatform.controller;

import com.eduplatform.dto.ApiResponse;
import com.eduplatform.dto.CourseDTO;
import com.eduplatform.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Gestión del catálogo de cursos")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Listar todos los cursos disponibles")
    public ResponseEntity<ApiResponse<List<CourseDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(courseService.getAllAvailable(), "Cursos obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID")
    public ResponseEntity<ApiResponse<CourseDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(courseService.getById(id), "Curso encontrado"));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar cursos por categoría")
    public ResponseEntity<ApiResponse<List<CourseDTO.Response>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok(courseService.getByCategory(category), "Cursos por categoría"));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar cursos por palabra clave")
    public ResponseEntity<ApiResponse<List<CourseDTO.Response>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(courseService.search(keyword), "Resultados de búsqueda"));
    }

    @GetMapping("/price-range")
    @Operation(summary = "Filtrar cursos por rango de precio")
    public ResponseEntity<ApiResponse<List<CourseDTO.Response>>> getByPriceRange(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.ok(courseService.getByPriceRange(min, max), "Cursos en rango de precio"));
    }

    @GetMapping("/categories")
    @Operation(summary = "Listar categorías disponibles")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(courseService.getCategories(), "Categorías disponibles"));
    }

    @PostMapping
    @Operation(summary = "Agregar nuevo curso")
    public ResponseEntity<ApiResponse<CourseDTO.Response>> create(@Valid @RequestBody CourseDTO.CreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(courseService.create(req), "Curso creado exitosamente"));
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Activar o desactivar un curso")
    public ResponseEntity<ApiResponse<CourseDTO.Response>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(courseService.toggleAvailability(id), "Disponibilidad actualizada"));
    }
}
