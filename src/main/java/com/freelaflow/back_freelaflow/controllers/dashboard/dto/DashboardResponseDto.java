package com.freelaflow.back_freelaflow.controllers.dashboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardResponseDto {
    private long totalClientes;
    private long projetosAtivos; // Contratos em andamento
    private long propostasPendentes;
    private Double receitaMensal; // Soma dos contratos do mês
}