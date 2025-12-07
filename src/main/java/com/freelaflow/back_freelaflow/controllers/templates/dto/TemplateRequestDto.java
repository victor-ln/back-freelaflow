package com.freelaflow.back_freelaflow.controllers.templates.dto;

import lombok.Data;

@Data
public class TemplateRequestDto {
    private String nome;
    private String descricao;
    private String filename;
    private String filepath;
    private String storageType;
    private Long freelancerId;
    // status não existe no banco ainda, ignoramos ou tratamos via lógica
}