package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
}
