package com.freelaflow.back_freelaflow.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tipo", "freelancer_id"})
        })
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;
}

