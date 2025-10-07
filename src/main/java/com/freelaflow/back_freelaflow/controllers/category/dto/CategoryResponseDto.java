package com.freelaflow.back_freelaflow.controllers.category.dto;

import com.freelaflow.back_freelaflow.models.Category;
import lombok.*;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String tipo;
    private Boolean ativo;
}
