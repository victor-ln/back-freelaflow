package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmailAndFreelancerId(String email, Long freelancerId);
    boolean existsByCpfCnpjAndFreelancerId(String cpfCnpj, Long freelancerId);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
    Page<Cliente> findByFreelancerId(Long id, Pageable pageable);
    Page<Cliente> findByFreelancerIdAndNomeContainingIgnoreCase(Long freelancerId, String nome, Pageable pageable);
    long countByFreelancerId(Long freelancerId);
}
