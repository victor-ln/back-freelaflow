package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Freelancer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {
    Page<Freelancer> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    boolean existsByEmailOrCpfCnpj(String email, String cpf_cnpj);
    boolean existsByEmail(String email);
    boolean existsByCpfCnpj(String cpfCnpj);
    Optional<Freelancer> findByEmail(String email);
}
