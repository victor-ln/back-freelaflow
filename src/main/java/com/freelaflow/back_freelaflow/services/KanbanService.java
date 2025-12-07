package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.models.Kanban;
import com.freelaflow.back_freelaflow.repository.KanbanRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import jakarta.persistence.criteria.Predicate;
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

    public KanbanService(KanbanRepository kanbanRepository) {
        this.kanbanRepository = kanbanRepository;
    }

    public Map<String, Object> getKanbans(int page, int limit, Boolean active, Long proposalId) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());

        Specification<Kanban> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (active != null) {
                predicates.add(cb.equal(root.get("ativo"), active));
            }
            if (proposalId != null) {
                predicates.add(cb.equal(root.get("proposta").get("id"), proposalId));
            }

            // Assumindo que queremos filtrar por freelancer (padrão ID 1 se não vier no contexto futuramente)
            // predicates.add(cb.equal(root.get("freelancer").get("id"), 1L));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Kanban> result = kanbanRepository.findAll(spec, pageable);
        return PaginationUtils.toPaginatedResponse(result, k -> k);
    }
}