package com.freelaflow.back_freelaflow.controllers.templates;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.templates.dto.TemplateRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.models.Template;
import com.freelaflow.back_freelaflow.services.FileStorageService;
import com.freelaflow.back_freelaflow.services.TemplateService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/templates")
public class TemplateController {

    private final TemplateService templateService;
    private final FileStorageService fileStorageService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public TemplateController(TemplateService templateService,
                             FileStorageService fileStorageService,
                             GlobalExceptionHandler globalExceptionHandler) {
        this.templateService = templateService;
        this.fileStorageService = fileStorageService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<Template>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long freelancerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Long fId = (freelancerId != null) ? freelancerId : 1L;
        return globalExceptionHandler.handlePaginatedSuccess(
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

    /**
     * Upload de template DOCX
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTemplate(
            @RequestParam("nome") String nome,
            @RequestParam("descricao") String descricao,
            @RequestParam("freelancerId") Long freelancerId,
            @RequestParam("file") MultipartFile file) {

        Template template = templateService.uploadTemplate(nome, descricao, freelancerId, file);
        return globalExceptionHandler.handleCreateSuccess("Template enviado para revisão", template);
    }

    /**
     * Aprova ou rejeita um template
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        Template template = templateService.updateStatus(id, status);
        return globalExceptionHandler.handleSuccess("Status atualizado", template);
    }

    /**
     * Download do arquivo do template
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable Long id) {
        Template template = templateService.findById(id);
        Resource resource = fileStorageService.loadFileAsResource(template.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + template.getNome() + ".docx\"")
                .body(resource);
    }

    /**
     * Extrai variáveis do template
     */
    @GetMapping("/{id}/variables")
    public ResponseEntity<Map<String, Object>> getTemplateVariables(@PathVariable Long id) throws IOException {
        List<String> variables = templateService.getTemplateVariables(id);
        return globalExceptionHandler.handleSuccess("Variáveis extraídas", variables);
    }
}