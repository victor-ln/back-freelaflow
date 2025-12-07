package com.freelaflow.back_freelaflow.controllers.clients;

import com.freelaflow.back_freelaflow.controllers.clients.dto.ClientRequestDto;
import com.freelaflow.back_freelaflow.controllers.clients.dto.ClienteResponseDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.models.Cliente;
import com.freelaflow.back_freelaflow.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/clients")
public class ClienteController {
    private final ClienteService clienteService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public ClienteController(ClienteService clienteService, GlobalExceptionHandler globalExceptionHandler) {
        this.clienteService = clienteService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createClient(@Valid @RequestBody ClientRequestDto dto) {
        Cliente cliente =  clienteService.createClient(dto);
        return globalExceptionHandler.handleSuccess("Sucesso", cliente);
    }

    @GetMapping("/{id}/freelancer")
    public ResponseEntity<Map<String, Object>> getAllClientsByFreelancer(@PathVariable Long id,
                                                                         @RequestParam(defaultValue = "1") int page,
                                                                         @RequestParam(defaultValue = "10") int limit,
                                                                         @RequestParam(required = false) String search) {
        return globalExceptionHandler.handleSuccess("Sucesso", clienteService.getAllClientsByFreelancer(id, page, limit, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getClient(@PathVariable Long id) {
        return globalExceptionHandler.handleSuccess("Sucesso", clienteService.getClient(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateClient(
            @PathVariable Long id,
            @RequestBody ClientRequestDto dto) {
        return globalExceptionHandler.handleSuccess("Sucesso", clienteService.updateClient(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteClient(@PathVariable Long id) {
        clienteService.deleteClient(id);
        return globalExceptionHandler.handleSuccess("Sucesso",null);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<Map<String, Object>> getClientByEmail(@PathVariable String email) {
        return globalExceptionHandler.handleSuccess("Sucesso",clienteService.getClientByEmail(email));
    }

    @GetMapping("/by-document/{cpfCnpj}")
    public ResponseEntity<Map<String, Object>> getClientByCpfCnpj(@PathVariable String cpfCnpj) {
        return globalExceptionHandler.handleSuccess("Sucesso",clienteService.getClientByCpfCnpj(cpfCnpj));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) Long freelancerId, // O BFF deve enviar isso
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search) {
        
        // Fallback para evitar erro se o BFF não enviar (idealmente o BFF deve enviar)
        if (freelancerId == null) freelancerId = 1L; 

        // Reutilizando o método existente do service
        return globalExceptionHandler.handleSuccess("Sucesso", clienteService.getAllClientsByFreelancer(freelancerId, page, limit, search));
    }
}
