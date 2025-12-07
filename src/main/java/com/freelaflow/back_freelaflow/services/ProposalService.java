package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.proposals.dto.ProposalRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.*;
import com.freelaflow.back_freelaflow.repository.*;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final ClienteRepository clienteRepository;
    private final FreelancerRepository freelancerRepository;
    private final KanbanRepository kanbanRepository;
    private final ContractRepository contractRepository;
    private final ServiceRepository serviceRepository;
    private final TemplateRepository templateRepository;

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

    // LISTAGEM COM FILTROS DINÂMICOS (Specifications)
    public PaginatedResponseDto<Proposal> findAll(Long freelancerId, int page, int limit, String search, String status, Long clienteId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());

        // Montagem dinâmica da Query
        Specification<Proposal> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro Obrigatório/Principal: Freelancer
            if (freelancerId != null) {
                predicates.add(cb.equal(root.get("freelancer").get("id"), freelancerId));
            }

            // 2. Filtro Opcional: Status
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 3. Filtro Opcional: Cliente
            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }

            // 4. Filtro Opcional: Busca por Descrição (Case Insensitive)
            if (search != null && !search.isEmpty()) {
                String searchLike = "%" + search.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("descricao")), searchLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Proposal> pageResult = proposalRepository.findAll(spec, pageable);
        return PaginationUtils.toPaginatedResponse(pageResult, p -> p);
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
        proposal.setStatus("PENDING"); 

        return proposalRepository.save(proposal);
    }

    @Transactional
    public Proposal update(Long id, ProposalRequestDto dto) {
        Proposal proposal = findById(id);
        proposal.setDescricao(dto.getDescricao());
        proposal.setValor(dto.getValor());
        return proposalRepository.save(proposal);
    }

    public void delete(Long id) {
        if (!proposalRepository.existsById(id)) throw new ResourceNotFoundException("Proposta não encontrada");
        proposalRepository.deleteById(id);
    }

    // AÇÕES ESPECÍFICAS
    
    @Transactional
    public Proposal accept(Long id) {
        Proposal proposal = findById(id);
        proposal.setStatus("ACCEPTED");
        Proposal saved = proposalRepository.save(proposal);
        createKanbanForProposal(saved);
        return saved;
    }

    @Transactional
    public Proposal reject(Long id, String reason) {
        Proposal proposal = findById(id);
        proposal.setStatus("REJECTED");
        return proposalRepository.save(proposal);
    }

    public Proposal updateStatus(Long id, String status) {
        Proposal proposal = findById(id);
        proposal.setStatus(status);
        return proposalRepository.save(proposal);
    }

    // GERAÇÃO DE CONTRATO
    @Transactional
    public Contract generateContract(Long proposalId) {
        Proposal proposal = findById(proposalId);
        
        var serviceOpt = serviceRepository.findByFreelancerIdAndAtivoTrue(proposal.getFreelancer().getId(), PageRequest.of(0,1)).getContent().stream().findFirst();
        var templateOpt = templateRepository.findByFreelancerId(proposal.getFreelancer().getId(), PageRequest.of(0,1)).getContent().stream().findFirst();

        if (serviceOpt.isEmpty() || templateOpt.isEmpty()) {
             return null; 
        }

        Contract contract = new Contract();
        contract.setFreelancer(proposal.getFreelancer());
        contract.setCliente(proposal.getCliente());
        contract.setService(serviceOpt.get());
        contract.setTemplate(templateOpt.get());
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