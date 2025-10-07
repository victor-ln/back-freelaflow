package com.freelaflow.back_freelaflow.controllers.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateDto {
    private Long freelancerId;
    private String tipo;
    private Boolean ativo;
}
