package com.freelaflow.back_freelaflow.controllers.freelancers;

import com.freelaflow.back_freelaflow.controllers.freelancers.dto.*;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.services.FreelancerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/freelancer")
public class FreelancerController {
    private final FreelancerService freelancerService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public FreelancerController(FreelancerService freelancerService, GlobalExceptionHandler globalExceptionHandler) {
        this.freelancerService = freelancerService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createFreelancer(@RequestBody FreelancerRequestDto freelancerRequestDto) {
        FreelancerReponseDto novo = freelancerService.salvarFreelancer(freelancerRequestDto);
        return globalExceptionHandler.handleSuccess("sucesso", novo);
    }
    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createFreelancerWithAdmin(@RequestBody FreelancerRequestDto freelancerRequestDto) {
        FreelancerReponseDto novo = freelancerService.salvarFreelancer(freelancerRequestDto);
        return globalExceptionHandler.handleSuccess("sucesso", novo);
    }
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getFreelancer(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int limit,
                                                @RequestParam(required = false) String search) {
        Map<String, Object> freelancers = freelancerService.listarFreelancers(page, limit, search);
        return globalExceptionHandler.handleSuccess("sucesso", freelancers);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> consultarFreelancer(@PathVariable Long id) {
        FreelancerReponseDto freelancers = freelancerService.consultarFreelancer(id);
        return globalExceptionHandler.handleSuccess("sucesso", freelancers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFreelancer(@PathVariable Long id, @RequestBody FreelancerUpdateDto dto) {
            freelancerService.updateFreelancer(id, dto);
            return globalExceptionHandler.handleSuccess("Sucesso ao atualizar Freelancer", null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFreelancer(@PathVariable Long id) {
        freelancerService.deleteFreelancer(id);
        return globalExceptionHandler.handleSuccess("Freelancer deletado com sucesso", null);
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto request) {

        freelancerService.updatePassword(id, request);
        return globalExceptionHandler.handleSuccess("Senha Atualizada", null);
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<Map<String, Object>>  updateFreelancerRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFreelancerRolesDto dto
    ) {
        Freelancer updatedFreelancer = freelancerService.updateRoles(id, dto);
        return globalExceptionHandler.handleSuccess("sucesso", updatedFreelancer);
    }
}
