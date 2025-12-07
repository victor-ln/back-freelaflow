package com.freelaflow.back_freelaflow.controllers.category;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryRequestDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryResponseDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryUpdateDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public CategoryController(CategoryService categoryService, GlobalExceptionHandler globalExceptionHandler) {
        this.categoryService = categoryService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CategoryRequestDto dto) {
        CategoryResponseDto response = categoryService.create(dto);
        return globalExceptionHandler.handleSuccess("Sucesso ao criar categoria", response);
    }

    // Endpoint genérico que o BFF está chamando
    @GetMapping
    public ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> listar(
            @RequestParam(required = false) Long freelancerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String search
    ) {
        if (freelancerId == null) freelancerId = 1L; // Fallback
        return globalExceptionHandler.handlePaginatedSuccess(categoryService.listarCategorias(freelancerId, search, ativo, page, limit));
    }

    @GetMapping("/{id}/freelancer")
    public ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> listarCategorias(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String search
    ) {
        return globalExceptionHandler.handlePaginatedSuccess(categoryService.listarCategorias(id, search, ativo, page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoria(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Sucesso na consulta", categoryService.getCategoria(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateDto dto) {

        CategoryResponseDto updated = categoryService.updateCategory(id, dto);
        return globalExceptionHandler.handleSuccess("Sucesso na edição", updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoria(id);
        return globalExceptionHandler.handleSuccess("Deletado com sucesso", null);
    }
}