package com.freelaflow.back_freelaflow.controllers.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message = "O tipo da categoria é obrigatório.")
    private String tipo;

    @NotNull(message = "O ID do freelancer é obrigatório.")
    private Long freelancerId;

    // Adicionado para receber o status (opcional, default true via lógica ou front)
    private Boolean ativo; 
}