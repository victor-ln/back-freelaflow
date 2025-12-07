package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.kanban.dto.KanbanRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Kanban;
import com.freelaflow.back_freelaflow.models.Proposal;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.KanbanRepository;
import com.freelaflow.back_freelaflow.repository.ProposalRepository;
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
import java.util.List;
import java.util.Map;

@Service
public class KanbanService {

    private final KanbanRepository kanbanRepository;
    private final FreelancerRepository freelancerRepository;
    private final ProposalRepository proposalRepository;

    public KanbanService(KanbanRepository kanbanRepository, FreelancerRepository freelancerRepository, ProposalRepository proposalRepository) {
        this.kanbanRepository = kanbanRepository;
        this.freelancerRepository = freelancerRepository;
        this.proposalRepository = proposalRepository;
    }

    public Map<String, Object> listar(int page, int limit, Long freelancerId, Boolean ativo) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());

        Specification<Kanban> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (freelancerId != null) predicates.add(cb.equal(root.get("freelancer").get("id"), freelancerId));
            if (ativo != null) predicates.add(cb.equal(root.get("ativo"), ativo));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Kanban> pageResult = kanbanRepository.findAll(spec, pageable);
        return PaginationUtils.toPaginatedResponse(pageResult, k -> k);
    }

    public Kanban findById(Long id) {
        return kanbanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanban não encontrado"));
    }

    @Transactional
    public Kanban create(KanbanRequestDto dto) {
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));
        
        Kanban kanban = new Kanban();
        kanban.setTitulo(dto.getTitulo());
        kanban.setDescricao(dto.getDescricao());
        kanban.setFreelancer(freelancer);
        kanban.setAtivo(true);

        if (dto.getPropostaId() != null) {
            Proposal proposal = proposalRepository.findById(dto.getPropostaId()).orElse(null);
            kanban.setProposta(proposal);
        }

        return kanbanRepository.save(kanban);
    }

    @Transactional
    public Kanban update(Long id, KanbanRequestDto dto) {
        Kanban kanban = findById(id);
        kanban.setTitulo(dto.getTitulo());
        kanban.setDescricao(dto.getDescricao());
        if (dto.getAtivo() != null) kanban.setAtivo(dto.getAtivo());
        return kanbanRepository.save(kanban);
    }

    public void delete(Long id) {
        if (!kanbanRepository.existsById(id)) throw new ResourceNotFoundException("Kanban não encontrado");
        kanbanRepository.deleteById(id);
    }
}