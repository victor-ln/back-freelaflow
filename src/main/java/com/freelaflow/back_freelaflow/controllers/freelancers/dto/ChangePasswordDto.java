package com.freelaflow.back_freelaflow.controllers.freelancers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {

    @NotBlank(message = "Nova Senha não deve ser null")
    private String novaSenhaHash;
}
