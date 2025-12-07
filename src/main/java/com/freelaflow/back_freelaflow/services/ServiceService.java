package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.controllers.services.dto.ServiceRequestDto;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Category;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Service;
import com.freelaflow.back_freelaflow.repository.CategoryRepository;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.ServiceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    public Page<Service> listar(Long freelancerId, String search, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        if (search != null && !search.isBlank()) {
            return serviceRepository.findByFreelancerIdAndNomeContainingIgnoreCaseAndAtivoTrue(freelancerId, search, pageable);
        }
        return serviceRepository.findByFreelancerIdAndAtivoTrue(freelancerId, pageable);
    }
    
    public Service getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado"));
    }

    public void delete(Long id) {
        Service service = getById(id);
        service.setAtivo(false); // Soft Delete
        serviceRepository.save(service);
    }
}