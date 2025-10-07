package com.freelaflow.back_freelaflow.controllers.freelancers.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateFreelancerRolesDto {
    @NotEmpty(message = "A lista de roles não pode ser vazia")
    private List<String> roleNames;
}
