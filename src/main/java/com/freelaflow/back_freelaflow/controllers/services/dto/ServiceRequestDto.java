package com.freelaflow.back_freelaflow.controllers.services.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceRequestDto {
    private String nome;
    private String descricao;
    private Double valor;
    private Long categoriaId;
    private Long freelancerId;
    private Boolean ativo;
}