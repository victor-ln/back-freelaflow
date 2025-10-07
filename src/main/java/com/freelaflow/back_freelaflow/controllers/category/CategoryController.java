package com.freelaflow.back_freelaflow.controllers.category;

import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryRequestDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryResponseDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryUpdateDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


    @GetMapping("/{id}/freelancer")
    public ResponseEntity<Map<String, Object>> listarCategorias(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String search
    ) {
        return globalExceptionHandler.handleSuccess("Sucesso na consulta", categoryService.listarCategorias(id, search, ativo, page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoria(
            @PathVariable Long id
    ) {
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
    public ResponseEntity<Map<String, Object>> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategoria(id);
        return globalExceptionHandler.handleSuccess("Deletado com sucesso", null);
    }

}
