package com.freelaflow.back_freelaflow.controllers.freelancers.dto;

import com.freelaflow.back_freelaflow.models.Endereco;
import com.freelaflow.back_freelaflow.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FreelancerReponseDto {
    private Long id;
    private String nome;
    private String email;
    private String cpfCnpj;
    private boolean ativo;
    private Endereco endereco;
    private Set<Role> roles = new HashSet<>();
}
