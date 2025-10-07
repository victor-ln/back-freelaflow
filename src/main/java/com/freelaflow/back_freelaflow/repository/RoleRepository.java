package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Page<Role> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Role> findByNomeContainingIgnoreCaseAndAtivo(String nome, Boolean ativo, Pageable pageable);
    Page<Role> findByAtivo(Boolean ativo, Pageable pageable);
    Role findByNome(String nome);
    List<Role> findAllByNomeIn(List<String> nomes);
    List<Role> findByAtivoTrue();
    Optional<Role> findByNomeIgnoreCase(String nome);
}
