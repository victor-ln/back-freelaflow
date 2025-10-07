package com.freelaflow.back_freelaflow.controllers.freelancers.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FreelancerUpdateDto {
    private String nome;
    private String email;
    private String cpfCnpj;
    private EnderecoDto endereco;
    private boolean ativo;
}
