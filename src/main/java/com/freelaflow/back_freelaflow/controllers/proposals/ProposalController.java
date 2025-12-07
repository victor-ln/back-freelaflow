package com.freelaflow.back_freelaflow.controllers.proposals;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.proposals.dto.ProposalRequestDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.models.Contract;
import com.freelaflow.back_freelaflow.models.Proposal;
import com.freelaflow.back_freelaflow.services.ProposalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/proposals")
public class ProposalController {

    private final ProposalService proposalService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public ProposalController(ProposalService proposalService, GlobalExceptionHandler globalExceptionHandler) {
        this.proposalService = proposalService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody ProposalRequestDto dto) {
        return globalExceptionHandler.handleCreateSuccess("Proposta criada", proposalService.create(dto));
    }

    // Atende findAll, findByStatus e findByClient do BFF
    @GetMapping
    public ResponseEntity<PaginatedResponseDto<Proposal>> list(
            @RequestParam Long freelancerId, // Idealmente viria do Token/Contexto, mas o BFF passa via query ou DTO no findAll se ajustarmos, mas aqui assumo que o BFF manda
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long clienteId) {

        // Nota: O BFF do seu código não manda 'freelancerId' na query string explicitamente no método findAll,
        // mas manda filtros. Se o backend precisar filtrar por freelancer logado, o BFF teria que passar esse ID.
        // Assumirei que você ajustará o BFF para passar o freelancerId ou que estamos usando um fixo para teste se não vier.
        if (freelancerId == null) freelancerId = 1L; // Fallback para teste rápido

        PaginatedResponseDto<Proposal> result = proposalService.findAll(freelancerId, page, limit, search, status, clienteId);
        return globalExceptionHandler.handlePaginatedSuccess(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Proposta encontrada", proposalService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody ProposalRequestDto dto) {
        return globalExceptionHandler.handleSuccess("Proposta atualizada", proposalService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        proposalService.delete(id);
        return globalExceptionHandler.handleSuccess("Proposta removida", null);
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<Map<String, Object>> accept(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Proposta aceita", proposalService.accept(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> reject(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        return globalExceptionHandler.handleSuccess("Proposta rejeitada", proposalService.reject(id, reason));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return globalExceptionHandler.handleSuccess("Status atualizado", proposalService.updateStatus(id, body.get("status")));
    }

    @PostMapping("/{id}/generate-contract")
    public ResponseEntity<Map<String, Object>> generateContract(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long templateId = body.get("templateId");
        if (templateId == null) {
            throw new RuntimeException("templateId é obrigatório");
        }
        Contract contract = proposalService.generateContract(id, templateId);
        return globalExceptionHandler.handleSuccess("Contrato gerado com sucesso", contract);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(@RequestParam(required = false) Long freelancerId) {
        if (freelancerId == null) freelancerId = 1L; 
        return globalExceptionHandler.handleSuccess("Métricas", proposalService.getMetrics(freelancerId));
    }
}