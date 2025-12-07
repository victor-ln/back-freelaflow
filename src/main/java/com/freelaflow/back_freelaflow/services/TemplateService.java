package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.templates.dto.TemplateRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Template;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.TemplateRepository;
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
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final FreelancerRepository freelancerRepository;

    public TemplateService(TemplateRepository templateRepository, FreelancerRepository freelancerRepository) {
        this.templateRepository = templateRepository;
        this.freelancerRepository = freelancerRepository;
    }

    public Map<String, Object> listar(int page, int limit, Long freelancerId, String status, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());

        Specification<Template> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (freelancerId != null) predicates.add(cb.equal(root.get("freelancer").get("id"), freelancerId));
            if (search != null && !search.isBlank()) predicates.add(cb.like(cb.lower(root.get("nome")), "%" + search.toLowerCase() + "%"));
            // Status ignorado pois não existe no banco ainda
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Template> pageResult = templateRepository.findAll(spec, pageable);
        return PaginationUtils.toPaginatedResponse(pageResult, t -> t);
    }

    public Template findById(Long id) {
        return templateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Template não encontrado"));
    }

    @Transactional
    public Template create(TemplateRequestDto dto) {
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        Template template = new Template();
        template.setNome(dto.getNome());
        template.setDescricao(dto.getDescricao());
        template.setFilename(dto.getFilename());
        template.setFilepath(dto.getFilepath());
        template.setStorageType(dto.getStorageType() != null ? dto.getStorageType() : "LOCAL");
        template.setFreelancer(freelancer);

        return templateRepository.save(template);
    }
    
    @Transactional
    public Template update(Long id, TemplateRequestDto dto) {
        Template template = findById(id);
        template.setNome(dto.getNome());
        template.setDescricao(dto.getDescricao());
        return templateRepository.save(template);
    }

    public void delete(Long id) {
         if (!templateRepository.existsById(id)) throw new ResourceNotFoundException("Template não encontrado");
         templateRepository.deleteById(id);
    }
}