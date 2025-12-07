package com.freelaflow.back_freelaflow.controllers.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.services.dto.ServiceRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.models.Service;
import com.freelaflow.back_freelaflow.services.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/services")
public class ServiceController {

    private final ServiceService serviceService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public ServiceController(ServiceService serviceService, GlobalExceptionHandler globalExceptionHandler) {
        this.serviceService = serviceService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody ServiceRequestDto dto) {
        return globalExceptionHandler.handleCreateSuccess("Serviço criado", serviceService.create(dto));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<Service>> getAll(
            @RequestParam(required = false) Long freelancerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {

        if (freelancerId == null) freelancerId = 1L; // Fallback temporário
        return globalExceptionHandler.handlePaginatedSuccess(serviceService.listar(freelancerId, search, page, limit, status));
    }

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<PaginatedResponseDto<Service>> list(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        return globalExceptionHandler.handlePaginatedSuccess(serviceService.listar(id, search, page, limit, null));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return globalExceptionHandler.handleSuccess("Serviço removido", null);
    }
}