package com.freelaflow.back_freelaflow.controllers.templates;

import com.freelaflow.back_freelaflow.controllers.templates.dto.TemplateRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public TemplateController(TemplateService templateService, GlobalExceptionHandler globalExceptionHandler) {
        this.templateService = templateService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long freelancerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Long fId = (freelancerId != null) ? freelancerId : 1L;
        return globalExceptionHandler.handleSuccess("Lista de Templates",
                templateService.listar(page, limit, fId, status, search));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Template encontrado", templateService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TemplateRequestDto dto) {
        return globalExceptionHandler.handleCreateSuccess("Template criado", templateService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody TemplateRequestDto dto) {
        return globalExceptionHandler.handleSuccess("Template atualizado", templateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        templateService.delete(id);
        return globalExceptionHandler.handleSuccess("Template removido", null);
    }
}