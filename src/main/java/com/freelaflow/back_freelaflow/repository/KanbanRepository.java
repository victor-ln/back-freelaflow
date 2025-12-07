package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban, Long>, JpaSpecificationExecutor<Kanban> {
    Optional<Kanban> findByPropostaId(Long propostaId);
}