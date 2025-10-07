package com.freelaflow.back_freelaflow.controllers.roles.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDto {
    @JsonProperty("name")
    private String nome;
    @JsonProperty("description")
    private String descricao;
    @JsonProperty("isActive")
    private Boolean ativo;
}
