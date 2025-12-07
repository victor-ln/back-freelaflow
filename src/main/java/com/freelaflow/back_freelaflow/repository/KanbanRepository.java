package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Kanban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface KanbanRepository extends JpaRepository<Kanban, Long> {
    List<Kanban> findByFreelancerIdAndAtivoTrue(Long freelancerId);
    Optional<Kanban> findByPropostaId(Long propostaId);
}