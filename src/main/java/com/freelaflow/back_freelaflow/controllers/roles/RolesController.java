package com.freelaflow.back_freelaflow.controllers.roles;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleRequestDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleResponseDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleUpdateDto;
import com.freelaflow.back_freelaflow.exceptions.GlobalExceptionHandler;
import com.freelaflow.back_freelaflow.services.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/roles")
public class RolesController {
    private final RoleService roleService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public RolesController(RoleService roleService, GlobalExceptionHandler globalExceptionHandler) {
        this.roleService = roleService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody RoleRequestDto rolesRequestDto) {
        RoleResponseDto dto = roleService.salvarRole(rolesRequestDto);
        return globalExceptionHandler.handleCreateSuccess("Sucesso ao criar Role", dto);
    }
    @GetMapping
    public ResponseEntity<PaginatedResponseDto<RoleResponseDto>> getRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) String search) {

        PaginatedResponseDto<RoleResponseDto> resultado = roleService.getRoles(page, limit, ativo, search);
        return globalExceptionHandler.handlePaginatedSuccess(resultado);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarRole(
            @PathVariable Long id,
            @RequestBody RoleUpdateDto roleUpdateDto) {

        RoleResponseDto dto = roleService.atualizarRole(id, roleUpdateDto);
        return globalExceptionHandler.handleSuccess("Role atualizada com sucesso", dto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long id) {
        RoleResponseDto dto = roleService.getRoleById(id);
        return globalExceptionHandler.handleSuccess("Role encontrada com sucesso", dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarRole(@PathVariable Long id) {
        roleService.deletarRole(id);
        return globalExceptionHandler.handleSuccess("Role deletada com sucesso",null);
    }
}
