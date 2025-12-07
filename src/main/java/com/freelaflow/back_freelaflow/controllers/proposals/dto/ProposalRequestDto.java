package com.freelaflow.back_freelaflow.controllers.proposals.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProposalRequestDto {
    private String descricao;
    private Double valor;
    private Long clienteId;
    private Long freelancerId;
    private Long categoriaId;
    private String status;
}