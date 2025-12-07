package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long>, JpaSpecificationExecutor<Proposal> {
    
    // Métodos auxiliares para métricas (Mantidos pois são simples e diretos)
    long countByFreelancerIdAndStatus(Long freelancerId, String status);
    long countByFreelancerId(Long freelancerId);
}