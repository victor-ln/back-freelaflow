package com.freelaflow.back_freelaflow.controllers.kanban.dto;

import lombok.Data;

@Data
public class MoveTaskDto {
    private String novoStatus;
    private Integer novaOrdem; // Opcional, se implementar ordenação
}