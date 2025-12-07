package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleRequestDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleResponseDto;
import com.freelaflow.back_freelaflow.controllers.roles.dto.RoleUpdateDto;
import com.freelaflow.back_freelaflow.exceptions.ConflitException;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Role;
import com.freelaflow.back_freelaflow.repository.RoleRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleResponseDto salvarRole (RoleRequestDto rolesRequestDto) {
        Role role = new Role();
        role.setNome(rolesRequestDto.getNome());
        role.setDescricao(rolesRequestDto.getDescricao());
        role.setAtivo(rolesRequestDto.getAtivo());
        if (roleRepository.findByNome(rolesRequestDto.getNome()) != null) {
            throw new ConflitException("Role ja existente");
        }
        Role response = roleRepository.save(role);
        return rolesEntityToRoles(response);
    }

    public PaginatedResponseDto<RoleResponseDto> getRoles(int page, int limit, Boolean ativo, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Role> resultado;

        if (search != null && !search.isEmpty()) {
            if (ativo != null) {
                resultado = roleRepository.findByNomeContainingIgnoreCaseAndAtivo(search, ativo, pageable);
            } else {
                resultado = roleRepository.findByNomeContainingIgnoreCase(search, pageable);
            }
        } else {
            if (ativo != null) {
                resultado = roleRepository.findByAtivo(ativo, pageable);
            } else {
                resultado = roleRepository.findAll(pageable);
            }
        }

        return PaginationUtils.toPaginatedResponse(resultado, this::rolesEntityToRoles);
    }

    public RoleResponseDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));
        return rolesEntityToRoles(role);
    }
    public void deletarRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));

        roleRepository.delete(role);
    }
    public RoleResponseDto atualizarRole(Long id, RoleUpdateDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));

        if (dto.getName() != null) {
            Role roleExistente = roleRepository.findByNome(dto.getName());
            if (roleExistente != null && !roleExistente.getId().equals(id)) {
                throw new ConflitException("Já existe uma Role com esse nome");
            }
            role.setNome(dto.getName());
        }

        if (dto.getDescription() != null) role.setDescricao(dto.getDescription());
        if (dto.getIsActive() != null) role.setAtivo(dto.getIsActive());

        Role updated = roleRepository.save(role);
        return rolesEntityToRoles(updated);
    }
    public RoleResponseDto rolesEntityToRoles (Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setAtivo(role.getAtivo());
        dto.setDescricao(role.getDescricao());
        dto.setNome(role.getNome());
        return dto;
    }
}
