package com.freelaflow.back_freelaflow.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freelaflow.back_freelaflow.controllers.clients.dto.ClienteResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String cpfCnpj;

    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

}