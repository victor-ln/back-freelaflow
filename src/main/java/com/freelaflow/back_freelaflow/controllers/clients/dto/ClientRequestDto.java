package com.freelaflow.back_freelaflow.controllers.clients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequestDto {

    @NotNull(message = "O Cliente deve pertencer a um freelancer")
    private Long freelancer_id;

    @NotBlank(message = "O Cliente dever informar o nome")
    private String nome;

    @Email
    private String email;

    @NotBlank(message = "O Cliente dever informar o cpf")
    private String cpfCnpj;

    private String telefone;

    private EnderecoDto endereco;
}
