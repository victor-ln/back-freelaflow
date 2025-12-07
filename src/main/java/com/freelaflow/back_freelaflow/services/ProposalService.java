package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.dashboard.dto.DashboardResponseDto; // Reutilizando DTO ou crie um específico
import com.freelaflow.back_freelaflow.controllers.proposals.dto.ProposalRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.*;
import com.freelaflow.back_freelaflow.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final ClienteRepository clienteRepository;
    private final FreelancerRepository freelancerRepository;
    private final KanbanRepository kanbanRepository;
    private final ContractRepository contractRepository;
    private final ServiceRepository serviceRepository; // Assumindo que o contrato precisa de um serviço
    private final TemplateRepository templateRepository; // Assumindo que o contrato precisa de um template

    public ProposalService(ProposalRepository proposalRepository, ClienteRepository clienteRepository, 
                           FreelancerRepository freelancerRepository, KanbanRepository kanbanRepository,
                           ContractRepository contractRepository, ServiceRepository serviceRepository,
                           TemplateRepository templateRepository) {
        this.proposalRepository = proposalRepository;
        this.clienteRepository = clienteRepository;
        this.freelancerRepository = freelancerRepository;
        this.kanbanRepository = kanbanRepository;
        this.contractRepository = contractRepository;
        this.serviceRepository = serviceRepository;
        this.templateRepository = templateRepository;
    }

    // LISTAGEM COM FILTROS (Atende findAll, findByStatus, findByClient)
    public Page<Proposal> findAll(Long freelancerId, int page, int limit, String search, String status, Long clienteId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return proposalRepository.findByFilters(freelancerId, status, clienteId, search, pageable);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proposta não encontrada"));
    }

    @Transactional
    public Proposal create(ProposalRequestDto dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        Proposal proposal = new Proposal();
        proposal.setDescricao(dto.getDescricao());
        proposal.setValor(dto.getValor());
        proposal.setCliente(cliente);
        proposal.setFreelancer(freelancer);
        proposal.setStatus("PENDING"); // Enum do BFF usa PENDING

        return proposalRepository.save(proposal);
    }

    @Transactional
    public Proposal update(Long id, ProposalRequestDto dto) {
        Proposal proposal = findById(id);
        proposal.setDescricao(dto.getDescricao());
        proposal.setValor(dto.getValor());
        // Atualizar outros campos se necessário
        return proposalRepository.save(proposal);
    }

    public void delete(Long id) {
        if (!proposalRepository.existsById(id)) throw new ResourceNotFoundException("Proposta não encontrada");
        proposalRepository.deleteById(id);
    }

    // AÇÕES ESPECÍFICAS (Aceitar/Rejeitar)
    
    @Transactional
    public Proposal accept(Long id) {
        Proposal proposal = findById(id);
        proposal.setStatus("ACCEPTED");
        Proposal saved = proposalRepository.save(proposal);
        
        // Regra de Negócio: Criar Kanban ao aceitar
        createKanbanForProposal(saved);
        return saved;
    }

    @Transactional
    public Proposal reject(Long id, String reason) {
        Proposal proposal = findById(id);
        proposal.setStatus("REJECTED");
        // O motivo (reason) poderia ser salvo se houvesse campo na tabela
        return proposalRepository.save(proposal);
    }

    public Proposal updateStatus(Long id, String status) {
        Proposal proposal = findById(id);
        proposal.setStatus(status);
        return proposalRepository.save(proposal);
    }

    // GERAÇÃO DE CONTRATO (Mínimo para funcionar)
    @Transactional
    public Contract generateContract(Long proposalId) {
        Proposal proposal = findById(proposalId);
        
        // Lógica simplificada: pega o primeiro serviço e template do freelancer para não quebrar
        // No mundo real, isso viria no DTO de entrada
        com.freelaflow.back_freelaflow.models.Service service = serviceRepository.findByFreelancerIdAndAtivoTrue(proposal.getFreelancer().getId(), PageRequest.of(0,1)).getContent().stream().findFirst().orElse(null);
        Template template = templateRepository.findByFreelancerId(proposal.getFreelancer().getId(), PageRequest.of(0,1)).getContent().stream().findFirst().orElse(null);

        if (service == null || template == null) {
             // Retorna nulo ou erro se não tiver dados para gerar contrato, mas não quebra a proposta
             return null; 
        }

        Contract contract = new Contract();
        contract.setFreelancer(proposal.getFreelancer());
        contract.setCliente(proposal.getCliente());
        contract.setService(service);
        contract.setTemplate(template);
        contract.setNomeArquivo("Contrato_" + proposal.getId() + ".pdf");
        contract.setCaminhoArquivo("/tmp/mock_contract.pdf");
        contract.setStatus("GERADO");
        
        return contractRepository.save(contract);
    }

    public Map<String, Object> getMetrics(Long freelancerId) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("total", proposalRepository.countByFreelancerId(freelancerId));
        metrics.put("accepted", proposalRepository.countByFreelancerIdAndStatus(freelancerId, "ACCEPTED"));
        metrics.put("pending", proposalRepository.countByFreelancerIdAndStatus(freelancerId, "PENDING"));
        metrics.put("rejected", proposalRepository.countByFreelancerIdAndStatus(freelancerId, "REJECTED"));
        return metrics;
    }

    private void createKanbanForProposal(Proposal proposal) {
        if (kanbanRepository.findByPropostaId(proposal.getId()).isPresent()) return;

        Kanban kanban = new Kanban();
        kanban.setTitulo("Projeto: " + proposal.getCliente().getNome());
        kanban.setDescricao(proposal.getDescricao());
        kanban.setFreelancer(proposal.getFreelancer());
        kanban.setProposta(proposal);
        kanban.setAtivo(true);
        kanbanRepository.save(kanban);
    }
}