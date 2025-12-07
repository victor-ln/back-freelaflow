package com.freelaflow.back_freelaflow.services;

import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.freelancers.dto.*;
import com.freelaflow.back_freelaflow.exceptions.ConflitException;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Endereco;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.models.Role;
import com.freelaflow.back_freelaflow.repository.EnderecoRepository;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.repository.RoleRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreelancerService {
    private final FreelancerRepository freelancerRepository;
    private final EnderecoRepository enderecoRepository;
    private final RoleRepository roleRepository;

    public FreelancerService(FreelancerRepository freelancerRepository, EnderecoRepository enderecoRepository, RoleRepository roleRepository) {
        this.freelancerRepository = freelancerRepository;
        this.enderecoRepository = enderecoRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public FreelancerReponseDto salvarFreelancer(FreelancerRequestDto dto) {

        if (freelancerRepository.existsByEmailOrCpfCnpj(dto.getEmail(),dto.getCpfCnpj())) {
            throw  new ConflitException("Email ou Cpf já existente");
        }

        Endereco endereco = new Endereco();
        endereco.setCep(dto.getEndereco().getCep());
        endereco.setRua(dto.getEndereco().getRuaAvenida());
        endereco.setNumero(dto.getEndereco().getNumero());
        endereco.setComplemento(dto.getEndereco().getComplemento());
        endereco.setBairro(dto.getEndereco().getBairro());
        endereco.setCidade(dto.getEndereco().getCidade());
        endereco.setEstado(dto.getEndereco().getEstado());
        endereco.setPais(dto.getEndereco().getPais());

        Freelancer freelancer = new Freelancer();
        freelancer.setNome(dto.getNome());
        freelancer.setEmail(dto.getEmail());
        freelancer.setAtivo(true);
        freelancer.setCpfCnpj(dto.getCpfCnpj());
        freelancer.setSenha(dto.getSenha());
        freelancer.setEndereco(endereco);

        dto.getRoles().forEach(roleName -> {
            Role role = roleRepository.findByNome(roleName);
            if (role != null) {
                freelancer.getRoles().add(role);
            } else {
                throw new ResourceNotFoundException("Role informada não encontrada");
            }
        });

        return freelancerEntityToFreelancerDto(freelancerRepository.save(freelancer));
    }

    public PaginatedResponseDto<FreelancerReponseDto> listarFreelancers(int page, int limit, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").ascending());
        Page<Freelancer> resultado;

        if (search == null || search.isEmpty()) {
            resultado = freelancerRepository.findAll(pageable);
        } else {
            resultado = freelancerRepository.findByNomeContainingIgnoreCase(search, pageable);
        }
        for (Freelancer f : resultado) {
            System.out.println("Freelancer: " + f.getNome());
            System.out.println("Roles carregadas: " + f.getRoles().size());
            f.getRoles().forEach(r -> System.out.println(" - " + r.getNome()));
        }

        return PaginationUtils.toPaginatedResponse(resultado, this::freelancerEntityToFreelancerDto);
    }

    public FreelancerReponseDto consultarFreelancer(Long id) {
        Freelancer freelancer= freelancerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Erro ao encontrar Freelancer"));
        return freelancerEntityToFreelancerDto(freelancer);
    }
    public FreelancerReponseDto updateFreelancer(Long id, FreelancerUpdateDto dto) {
        Freelancer freelancer = freelancerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Freelancer não encontrado"));

        if (dto.getNome() != null) freelancer.setNome(dto.getNome());

        if (dto.getEmail() != null) {
            if (!dto.getEmail().equals(freelancer.getEmail()) && freelancerRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email já cadastrado");
            }
            freelancer.setEmail(dto.getEmail());
        }

        if (dto.getCpfCnpj() != null) {
            if (!dto.getCpfCnpj().equals(freelancer.getCpfCnpj()) && freelancerRepository.existsByCpfCnpj(dto.getCpfCnpj())) {
                throw new RuntimeException("CPF/CNPJ já cadastrado");
            }
            freelancer.setCpfCnpj(dto.getCpfCnpj());
        }

        if (dto.getEndereco() != null) {
            EnderecoDto enderecoDTO = dto.getEndereco();

            if (enderecoDTO.getId() != null) {
                // 🔁 Atualizar endereço existente
                Endereco enderecoExistente = enderecoRepository.findById(enderecoDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

                if (enderecoDTO.getCep() != null) enderecoExistente.setCep(enderecoDTO.getCep());
                if (enderecoDTO.getRuaAvenida() != null) enderecoExistente.setRua(enderecoDTO.getRuaAvenida());
                if (enderecoDTO.getNumero() != null) enderecoExistente.setNumero(enderecoDTO.getNumero());
                if (enderecoDTO.getComplemento() != null) enderecoExistente.setComplemento(enderecoDTO.getComplemento());
                if (enderecoDTO.getBairro() != null) enderecoExistente.setBairro(enderecoDTO.getBairro());
                if (enderecoDTO.getCidade() != null) enderecoExistente.setCidade(enderecoDTO.getCidade());
                if (enderecoDTO.getEstado() != null) enderecoExistente.setEstado(enderecoDTO.getEstado());
                if (enderecoDTO.getPais() != null) enderecoExistente.setPais(enderecoDTO.getPais());

                enderecoRepository.save(enderecoExistente);
                freelancer.setEndereco(enderecoExistente);
            } else {
                Endereco novo = new Endereco();
                novo.setCep(enderecoDTO.getCep());
                novo.setRua(enderecoDTO.getRuaAvenida());
                novo.setNumero(enderecoDTO.getNumero());
                novo.setComplemento(enderecoDTO.getComplemento());
                novo.setBairro(enderecoDTO.getBairro());
                novo.setCidade(enderecoDTO.getCidade());
                novo.setEstado(enderecoDTO.getEstado());
                novo.setPais(enderecoDTO.getPais());

                enderecoRepository.save(novo);
                freelancer.setEndereco(novo);
            }
        }

        return freelancerEntityToFreelancerDto(freelancerRepository.save(freelancer));
    }
    public void deleteFreelancer(Long id) {
        freelancerRepository.deleteById(id);
    }
    public void updatePassword(Long freelancerId, ChangePasswordDto request) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado."));

        freelancer.setSenha(request.getNovaSenhaHash());
        freelancerRepository.save(freelancer);
    }

    public Freelancer updateRoles(Long freelancerId, UpdateFreelancerRolesDto dto) {

        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new EntityNotFoundException("Freelancer não encontrado"));

        List<Role> roles = roleRepository.findAllByNomeIn(dto.getRoleNames());

        if (roles.size() != dto.getRoleNames().size()) {
            throw new IllegalArgumentException("Alguma das roles informadas não existe");
        }

        Set<Role> roleSet = new HashSet<>(roles);
        freelancer.setRoles(roleSet);

        return freelancerRepository.save(freelancer);
    }

    private FreelancerReponseDto freelancerEntityToFreelancerDto (Freelancer freelancer) {
        System.out.println(freelancer.getRoles());
        FreelancerReponseDto dto = new FreelancerReponseDto();
        dto.setId(freelancer.getId());
        dto.setAtivo(freelancer.getAtivo());
        dto.setNome(freelancer.getNome());
        dto.setEmail(freelancer.getEmail());
        dto.setCpfCnpj(freelancer.getCpfCnpj());
        dto.setEndereco(freelancer.getEndereco());
        dto.setRoles(freelancer.getRoles());
        dto.setSenha(freelancer.getSenha());
        return dto;
    }

    /**
     * Busca freelancer por email
     * 
     * @param email Email do freelancer
     * @return DTO de resposta do freelancer
     * @throws ResourceNotFoundException se não encontrado
     */
    public FreelancerReponseDto consultarFreelancerByEmail(String email) {
        Freelancer freelancer = freelancerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));
        return freelancerEntityToFreelancerDto(freelancer);
    }

    /**
     * Atualiza apenas o status ativo/inativo do freelancer
     * 
     * @param id ID do freelancer
     * @param ativo Novo status
     * @return DTO de resposta do freelancer atualizado
     */
    public FreelancerReponseDto updateStatus(Long id, Boolean ativo) {
        Freelancer freelancer = freelancerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));
        
        freelancer.setAtivo(ativo);
        freelancerRepository.save(freelancer);
        
        return freelancerEntityToFreelancerDto(freelancer);
    }
}
