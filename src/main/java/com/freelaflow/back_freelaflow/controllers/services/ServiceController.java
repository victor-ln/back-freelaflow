package com.freelaflow.back_freelaflow.controllers.services;

import com.freelaflow.back_freelaflow.controllers.services.dto.ServiceRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
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

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<Map<String, Object>> list(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        return globalExceptionHandler.handleSuccess("Lista de serviços", serviceService.listar(id, search, page, limit));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        serviceService.delete(id);
        return globalExceptionHandler.handleSuccess("Serviço removido", null);
    }
}