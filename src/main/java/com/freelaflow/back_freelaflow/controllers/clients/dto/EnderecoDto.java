package com.freelaflow.back_freelaflow.controllers.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EnderecoDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("cep")
    private String cep;
    @JsonProperty("ruaAvenida")
    private String ruaAvenida;
    @JsonProperty("numero")
    private String numero;
    @JsonProperty("complemento")
    private String complemento;
    @JsonProperty("bairro")
    private String bairro;
    @JsonProperty("cidade")
    private String cidade;
    @JsonProperty("estado")
    private String estado;
    @JsonProperty("pais")
    private String pais;
}
