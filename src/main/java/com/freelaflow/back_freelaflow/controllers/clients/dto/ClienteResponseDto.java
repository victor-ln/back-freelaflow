package com.freelaflow.back_freelaflow.controllers.clients.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String cpfCnpj;
    private String telefone;
    private EnderecoDto endereco;
    private Long frelancerId;
}
