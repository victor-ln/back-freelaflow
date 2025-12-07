package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.dashboard.dto.DashboardResponseDto;
import com.freelaflow.back_freelaflow.repository.ClienteRepository;
import com.freelaflow.back_freelaflow.repository.ContractRepository;
import com.freelaflow.back_freelaflow.repository.ProposalRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;

    public DashboardService(ClienteRepository clienteRepository, 
                            ContractRepository contractRepository, 
                            ProposalRepository proposalRepository) {
        this.clienteRepository = clienteRepository;
        this.contractRepository = contractRepository;
        this.proposalRepository = proposalRepository;
    }

    public DashboardResponseDto getStats(Long freelancerId) {
        DashboardResponseDto dto = new DashboardResponseDto();

        // 1. Total de Clientes (Real)
        dto.setTotalClientes(clienteRepository.countByFreelancerId(freelancerId));

        // 2. Projetos Ativos (Real)
        // Assume que o status no banco para contrato ativo seja "ATIVO" ou "GERADO"
        // Ajuste a string "ATIVO" conforme o que você salva no banco
        dto.setProjetosAtivos(contractRepository.countByFreelancerIdAndStatus(freelancerId, "ATIVO"));

        // 3. Propostas Pendentes (Real)
        // Assume que o status padrão é "Pendente" (conforme seu script SQL V1)
        dto.setPropostasPendentes(proposalRepository.countByFreelancerIdAndStatus(freelancerId, "Pendente"));

        // 4. Receita Mensal/Total (Real)
        // Soma o valor dos serviços de todos os contratos ativos
        Double receita = contractRepository.sumValorTotalByFreelancerIdAndStatus(freelancerId, "ATIVO");
        dto.setReceitaMensal(receita);

        return dto;
    }
}