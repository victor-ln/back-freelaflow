package com.freelaflow.back_freelaflow.services;

import ch.qos.logback.core.net.server.Client;
import com.freelaflow.back_freelaflow.common.dto.PaginatedResponseDto;
import com.freelaflow.back_freelaflow.controllers.clients.dto.ClientRequestDto;
import com.freelaflow.back_freelaflow.controllers.clients.dto.ClienteResponseDto;
import com.freelaflow.back_freelaflow.controllers.clients.dto.EnderecoDto;
import com.freelaflow.back_freelaflow.exceptions.ConflitException;
import com.freelaflow.back_freelaflow.exceptions.ResourceNotFoundException;
import com.freelaflow.back_freelaflow.models.Cliente;
import com.freelaflow.back_freelaflow.models.Endereco;
import com.freelaflow.back_freelaflow.models.Freelancer;
import com.freelaflow.back_freelaflow.repository.ClienteRepository;
import com.freelaflow.back_freelaflow.repository.EnderecoRepository;
import com.freelaflow.back_freelaflow.repository.FreelancerRepository;
import com.freelaflow.back_freelaflow.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FreelancerRepository freelancerRepository;
    private final EnderecoRepository enderecoRepository;

    public Cliente createClient(ClientRequestDto dto) {

        boolean emailExists = clienteRepository.existsByEmailAndFreelancerId(dto.getEmail(), dto.getFreelancer_id());
        if (emailExists) {
            throw new ConflitException("Já existe um cliente com esse e-mail para este freelancer.");
        }

        boolean cpfExists = clienteRepository.existsByCpfCnpjAndFreelancerId(dto.getCpfCnpj(), dto.getFreelancer_id());
        if (cpfExists) {
            throw new ConflitException("Já existe um cliente com esse CPF/CNPJ para este freelancer.");
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

        Cliente client = new Cliente();
        client.setNome(dto.getNome());
        client.setEmail(dto.getEmail());
        client.setCpfCnpj(dto.getCpfCnpj());
        client.setTelefone(dto.getTelefone());

        Freelancer freelancer = freelancerRepository.findById(dto.getFreelancer_id()).orElseThrow(() -> new ResourceNotFoundException("Freelancer não reconhecido"));
        client.setFreelancer(freelancer);

        client.setEndereco(endereco);

        client = clienteRepository.save(client);
        return client;
    }

    public PaginatedResponseDto<ClienteResponseDto> getAllClientsByFreelancer(Long id, int page, int limit, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Cliente> clientesPage;

        if (search != null && !search.isEmpty()) {
            clientesPage = clienteRepository.findByFreelancerIdAndNomeContainingIgnoreCase(id, search, pageable);
        } else {
            clientesPage = clienteRepository.findByFreelancerId(id, pageable);
        }

        return PaginationUtils.toPaginatedResponse(clientesPage, this::clientEntityToClienteDto);
    }

    public ClienteResponseDto getClient(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return clientEntityToClienteDto(cliente);
    }

    public ClienteResponseDto updateClient(Long id, ClientRequestDto dto) {
        Cliente client = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        if (dto.getNome() != null) client.setNome(dto.getNome());
        if (dto.getEmail() != null) client.setEmail(dto.getEmail());
        if (dto.getCpfCnpj() != null) client.setCpfCnpj(dto.getCpfCnpj());
        if (dto.getTelefone() != null) client.setTelefone(dto.getTelefone());
        if (dto.getEndereco() != null) {
            Endereco endereco = enderecoRepository.findById(dto.getEndereco().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));
            endereco.setCep(dto.getEndereco().getCep());
            endereco.setRua(dto.getEndereco().getRuaAvenida());
            endereco.setNumero(dto.getEndereco().getNumero());
            endereco.setComplemento(dto.getEndereco().getComplemento());
            endereco.setBairro(dto.getEndereco().getBairro());
            endereco.setCidade(dto.getEndereco().getCidade());
            endereco.setEstado(dto.getEndereco().getEstado());
            endereco.setPais(dto.getEndereco().getPais());
            client.setEndereco(endereco);
        }

        client = clienteRepository.save(client);
        return clientEntityToClienteDto(client);
    }

    public void deleteClient(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    public ClienteResponseDto getClientByEmail(String email) {
        Cliente client = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return clientEntityToClienteDto(client);
    }

    public ClienteResponseDto getClientByCpfCnpj(String cpfCnpj) {
        Cliente client = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        return clientEntityToClienteDto(client);
    }

    private ClienteResponseDto clientEntityToClienteDto(Cliente cliente) {
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getEmail());
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setTelefone(cliente.getTelefone());
        dto.setEndereco(cliente.getEndereco() != null ? enderecoEntityToEnderecoDto(cliente.getEndereco()) : null);
        dto.setFrelancerId(cliente.getFreelancer() != null ? cliente.getFreelancer().getId() : null);
        return dto;
    }

    private EnderecoDto enderecoEntityToEnderecoDto(Endereco endereco) {
        EnderecoDto dto = new EnderecoDto();
        dto.setCep(endereco.getCep());
        dto.setId(endereco.getId());
        dto.setRuaAvenida(endereco.getRua());
        dto.setNumero(endereco.getNumero());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setPais(endereco.getPais());
        return dto;
    }
}
