package com.freelaflow.back_freelaflow.controllers.roles.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDto {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
}
