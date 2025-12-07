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

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final FileStorageService fileStorageService;
    private final TemplateProcessorService templateProcessorService;

    public ProposalService(ProposalRepository proposalRepository, ClienteRepository clienteRepository,
                           FreelancerRepository freelancerRepository, KanbanRepository kanbanRepository,
                           ContractRepository contractRepository, ServiceRepository serviceRepository,
                           TemplateRepository templateRepository, FileStorageService fileStorageService,
                           TemplateProcessorService templateProcessorService) {
        this.proposalRepository = proposalRepository;
        this.clienteRepository = clienteRepository;
        this.freelancerRepository = freelancerRepository;
        this.kanbanRepository = kanbanRepository;
        this.contractRepository = contractRepository;
        this.serviceRepository = serviceRepository;
        this.templateRepository = templateRepository;
        this.fileStorageService = fileStorageService;
        this.templateProcessorService = templateProcessorService;
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
    public Contract generateContract(Long proposalId, Long templateId) {
        Proposal proposal = findById(proposalId);

        // Busca o template
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template não encontrado"));

        // Valida se o template está aprovado
        if (!"APROVADO".equals(template.getStatus())) {
            throw new RuntimeException("Template não está aprovado para uso");
        }

        // Busca um serviço ativo do freelancer
        var serviceOpt = serviceRepository.findByFreelancerIdAndAtivoTrue(
                proposal.getFreelancer().getId(), PageRequest.of(0, 1))
                .getContent().stream().findFirst();

        if (serviceOpt.isEmpty()) {
            throw new ResourceNotFoundException("Nenhum serviço ativo encontrado para o freelancer");
        }

        com.freelaflow.back_freelaflow.models.Service service = serviceOpt.get();

        // Prepara as variáveis para substituição
        Map<String, String> variables = buildContractVariables(proposal, service);

        try {
            // Gera o nome do arquivo processado
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String outputFilename = "Contrato_" + proposal.getId() + "_" + timestamp + ".docx";
            Path outputPath = fileStorageService.getFileStorageLocation().resolve(outputFilename);

            // Processa o template
            Path templatePath = Path.of(template.getFilepath());
            templateProcessorService.processTemplate(templatePath, outputPath, variables);

            // Cria o registro do contrato
            Contract contract = new Contract();
            contract.setFreelancer(proposal.getFreelancer());
            contract.setCliente(proposal.getCliente());
            contract.setService(service);
            contract.setTemplate(template);
            contract.setNomeArquivo(outputFilename);
            contract.setCaminhoArquivo(outputPath.toString());
            contract.setStatus("GERADO");

            return contractRepository.save(contract);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar template: " + e.getMessage(), e);
        }
    }

    /**
     * Constrói o mapa de variáveis para substituição no template
     */
    private Map<String, String> buildContractVariables(Proposal proposal, com.freelaflow.back_freelaflow.models.Service service) {
        Map<String, String> variables = new HashMap<>();

        // Variáveis do cliente
        variables.put("CLIENTE_NOME", proposal.getCliente().getNome());
        variables.put("CLIENTE_EMAIL", proposal.getCliente().getEmail() != null ? proposal.getCliente().getEmail() : "");
        variables.put("CLIENTE_TELEFONE", proposal.getCliente().getTelefone() != null ? proposal.getCliente().getTelefone() : "");

        // Variáveis do freelancer
        variables.put("FREELANCER_NOME", proposal.getFreelancer().getNome());
        variables.put("FREELANCER_EMAIL", proposal.getFreelancer().getEmail() != null ? proposal.getFreelancer().getEmail() : "");

        // Variáveis da proposta
        variables.put("VALOR_TOTAL", proposal.getValor() != null ? proposal.getValor().toString() : "0");
        variables.put("DESCRICAO", proposal.getDescricao() != null ? proposal.getDescricao() : "");

        // Variáveis do serviço
        variables.put("SERVICO_NOME", service.getNome());
        variables.put("SERVICO_DESCRICAO", service.getDescricao() != null ? service.getDescricao() : "");
        variables.put("PRECO_BASE", service.getPrecoBase() != null ? service.getPrecoBase().toString() : "0");

        // Variáveis de data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        variables.put("DATA_GERACAO", LocalDateTime.now().format(formatter));

        return variables;
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