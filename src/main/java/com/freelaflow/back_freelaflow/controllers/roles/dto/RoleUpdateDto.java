package com.freelaflow.back_freelaflow.controllers.roles.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateDto {
    private String name;
    private String description;
    private Boolean isActive;
}
