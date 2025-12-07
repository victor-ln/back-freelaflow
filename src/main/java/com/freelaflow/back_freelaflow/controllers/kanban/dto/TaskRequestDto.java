package com.freelaflow.back_freelaflow.controllers.kanban.dto;

import lombok.Data;

@Data
public class TaskRequestDto {
    private String titulo;
    private String descricao;
    private String status; // A Fazer, Em Progresso, Concluido
    private String prioridade; // Baixa, Media, Alta
    private Long kanbanId;
    private Long freelancerId;
}