package com.freelaflow.back_freelaflow.controllers.proposals.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProposalRequestDto {
    private String titulo;
    private String descricao;
    private Double valor;
    private Long clienteId;
    private Long freelancerId;
    private Long categoriaId;
    private Long templateId;
    private String status;
}