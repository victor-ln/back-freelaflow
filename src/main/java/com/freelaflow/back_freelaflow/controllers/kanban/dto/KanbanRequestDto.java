package com.freelaflow.back_freelaflow.controllers.kanban.dto;

import lombok.Data;

@Data
public class KanbanRequestDto {
    private String titulo;
    private String descricao;
    private Long freelancerId;
    private Long propostaId;
    private Boolean ativo;
}