package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryRequestDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryResponseDto;
import com.freelaflow.back_freelaflow.controllers.category.dto.CategoryUpdateDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Category;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.repository.CategoryRepository;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FreelancerRepository freelancerRepository;

    public PaginatedResponseDto<CategoryResponseDto> listarCategorias(Long idfreelancer, String search, Boolean ativo, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("tipo").ascending());
        
        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (idfreelancer != null) {
                predicates.add(cb.equal(root.get("freelancer").get("id"), idfreelancer));
            }
            
            if (ativo != null) {
                predicates.add(cb.equal(root.get("ativo"), ativo));
            }

            if (search != null && !search.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("tipo")), "%" + search.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Category> categorias = categoryRepository.findAll(spec, pageable);
        // Removi a exceção "Categorias não encontradas" se a lista for vazia, pois em APIs REST retornar lista vazia [] é melhor que erro 404
        return PaginationUtils.toPaginatedResponse(categorias, this::categoryEntityToCategoryDto);
    }

    public CategoryResponseDto getCategoria(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return categoryEntityToCategoryDto(category);
    }

    @Transactional
    public CategoryResponseDto create(CategoryRequestDto dto) {
        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancerId())
                .orElseThrow(() -> new RuntimeException("Freelancer não encontrado."));

        if (categoryRepository.existsByTipoAndFreelancerId(dto.getTipo(), dto.getFreelancerId())) {
            throw new RuntimeException("Já existe uma categoria com esse tipo para este freelancer.");
        }
        
        Category category = new Category();
        category.setTipo(dto.getTipo());
        category.setFreelancer(freelancer);
        // Se vier null, assume true, senão usa o valor enviado
        category.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);

        Category saved = categoryRepository.save(category);

        return categoryEntityToCategoryDto(saved);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryUpdateDto dto) {
        Category category = categoryRepository.findById(id)
                .filter(c -> c.getFreelancer().getId().equals(dto.getFreelancerId()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        if (dto.getTipo() != null && !dto.getTipo().isBlank()) {
            boolean exists = categoryRepository.existsByTipoAndFreelancerId(dto.getTipo(), dto.getFreelancerId());
            if (exists && !category.getTipo().equals(dto.getTipo())) {
                throw new IllegalArgumentException("Já existe uma categoria com esse tipo");
            }
            category.setTipo(dto.getTipo());
        }

        if (dto.getAtivo() != null) {
            category.setAtivo(dto.getAtivo());
        }

        categoryRepository.save(category);

        return categoryEntityToCategoryDto(category);
    }

    public void deleteCategoria(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto categoryEntityToCategoryDto(Category entity) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }
}