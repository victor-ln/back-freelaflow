package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.services.dto.ServiceRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Category;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Service;
import com.freelaflow.back_freelaflow.repository.CategoryRepository;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.ServiceRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final FreelancerRepository freelancerRepository;
    private final CategoryRepository categoryRepository;

    public ServiceService(ServiceRepository serviceRepository, FreelancerRepository freelancerRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.freelancerRepository = freelancerRepository;
        this.categoryRepository = categoryRepository;
    }

    public Service create(ServiceRequestDto dto) {
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));
        Category category = categoryRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Service service = new Service();
        service.setNome(dto.getNome());
        service.setDescricao(dto.getDescricao());
        service.setValor(dto.getValor());
        service.setFreelancer(freelancer);
        service.setCategoria(category);
        
        return serviceRepository.save(service);
    }

    public PaginatedResponseDto<Service> listar(Long freelancerId, String search, int page, int limit, String status) {
        Pageable pageable = PageRequest.of(page - 1, limit);

        Specification<Service> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (freelancerId != null) {
                predicates.add(cb.equal(root.get("freelancer").get("id"), freelancerId));
            }

            if (status != null) {
                 // Assumindo que status é String no banco ("ATIVO", etc)
                 predicates.add(cb.equal(root.get("status"), status));
            }

            // Filtra ativos por padrão se não especificado o contrário na lógica de negócio,
            // ou mantém o campo 'ativo' booleano
            predicates.add(cb.equal(root.get("ativo"), true));

            if (search != null && !search.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + search.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Service> pageResult = serviceRepository.findAll(spec, pageable);
        return PaginationUtils.toPaginatedResponse(pageResult, s -> s);
    }
    
    public Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
    }

    public Service update(Long id, ServiceRequestDto dto) {
        Service service = getById(id);

        if (dto.getNome() != null) service.setNome(dto.getNome());
        if (dto.getDescricao() != null) service.setDescricao(dto.getDescricao());
        if (dto.getValor() != null) service.setValor(dto.getValor());

        if (dto.getCategoriaId() != null) {
            Category category = categoryRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            service.setCategoria(category);
        }

        return serviceRepository.save(service);
    }

    public Service updateStatus(Long id, String status) {
        Service service = getById(id);
        service.setStatus(status);
        return serviceRepository.save(service);
    }

    public void delete(Long id) {
        Service service = getById(id);
        service.setAtivo(false);
        serviceRepository.save(service);
    }
}