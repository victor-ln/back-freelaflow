package com.freelaflow.back_freelaflow.controllers.freelancers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FreelancerRequestDto {

    @JsonProperty("nome")
    private String nome;
    @JsonProperty("email")
    private String email;
    @JsonProperty("senha")
    private String senha;
    @JsonProperty("cpfCnpj")
    private String cpfCnpj;
    @JsonProperty("endereco")
    private EnderecoDto endereco;
    @JsonProperty("roles")
    private List<String> roles;
}
