package com.freelaflow.back_freelaflow.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "proposal")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valor_total")
    private Double valorTotal;

    @Column(nullable = false)
    private String status = "Pendente"; // Padrão: Pendente, Enviada, Aceita, Cancelada

    @Column(name = "contrato_status")
    private String contratoStatus = "Não Gerado"; // Não Gerado, Gerado, Editado Manualmente, Enviado, Assinado

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "evidencia_aceite")
    private String evidenciaAceite; // URL ou caminho da evidência

    @Column(name = "contrato_gerado")
    private String contratoGerado; // Caminho do contrato gerado

    @Column(name = "contrato_editado")
    private String contratoEditado; // Caminho do contrato editado

    @Column(name = "data_edicao_contrato")
    private LocalDateTime dataEdicaoContrato;

    @Column(name = "email_enviado")
    private Boolean emailEnviado = false;

    @Column(name = "data_envio_email")
    private LocalDateTime dataEnvioEmail;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Category categoria;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;

    @ManyToMany
    @JoinTable(
        name = "proposal_service",
        joinColumns = @JoinColumn(name = "proposal_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> servicos = new HashSet<>();

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}