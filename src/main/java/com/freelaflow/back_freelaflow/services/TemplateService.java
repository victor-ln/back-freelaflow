package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final FreelancerRepository freelancerRepository;
    private final FileStorageService fileStorageService;
    private final TemplateProcessorService templateProcessorService;

    public TemplateService(TemplateRepository templateRepository,
                          FreelancerRepository freelancerRepository,
                          FileStorageService fileStorageService,
                          TemplateProcessorService templateProcessorService) {
        this.templateRepository = templateRepository;
        this.freelancerRepository = freelancerRepository;
        this.fileStorageService = fileStorageService;
        this.templateProcessorService = templateProcessorService;
    }

    public PaginatedResponseDto<Template> listar(int page, int limit, Long freelancerId, String status, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());

        Specification<Template> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (freelancerId != null) predicates.add(cb.equal(root.get("freelancer").get("id"), freelancerId));
            if (status != null && !status.isBlank()) predicates.add(cb.equal(root.get("status"), status));
            if (search != null && !search.isBlank()) predicates.add(cb.like(cb.lower(root.get("nome")), "%" + search.toLowerCase() + "%"));
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
         Template template = findById(id);

         // Deleta o arquivo físico se existir
         if (template.getFilename() != null) {
             fileStorageService.deleteFile(template.getFilename());
         }

         templateRepository.deleteById(id);
    }

    /**
     * Faz upload de um template DOCX
     */
    @Transactional
    public Template uploadTemplate(String nome, String descricao, Long freelancerId, MultipartFile file) {
        // Valida o arquivo
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".docx")) {
            throw new RuntimeException("Apenas arquivos DOCX são permitidos");
        }

        // Busca o freelancer
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        // Armazena o arquivo
        String storedFilename = fileStorageService.storeFile(file);
        String filepath = fileStorageService.getFileStorageLocation().resolve(storedFilename).toString();

        // Cria o template
        Template template = new Template();
        template.setNome(nome);
        template.setDescricao(descricao);
        template.setFilename(storedFilename);
        template.setFilepath(filepath);
        template.setStorageType("LOCAL");
        template.setStatus("EM_REVISAO");
        template.setFreelancer(freelancer);

        return templateRepository.save(template);
    }

    /**
     * Aprova ou rejeita um template
     */
    @Transactional
    public Template updateStatus(Long id, String status) {
        Template template = findById(id);

        // Valida o status
        if (!status.equals("APROVADO") && !status.equals("REJEITADO") && !status.equals("EM_REVISAO")) {
            throw new RuntimeException("Status inválido. Use: EM_REVISAO, APROVADO ou REJEITADO");
        }

        template.setStatus(status);
        return templateRepository.save(template);
    }

    /**
     * Extrai variáveis de um template
     */
    public List<String> getTemplateVariables(Long id) throws IOException {
        Template template = findById(id);
        Path templatePath = Path.of(template.getFilepath());
        return templateProcessorService.extractVariables(templatePath);
    }
}