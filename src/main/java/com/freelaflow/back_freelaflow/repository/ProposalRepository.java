package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    
    // Query dinâmica para atender a todos os filtros do BFF (findAll, findByStatus, findByClient)
    @Query("SELECT p FROM Proposal p WHERE " +
           "(:freelancerId IS NULL OR p.freelancer.id = :freelancerId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:clienteId IS NULL OR p.cliente.id = :clienteId) AND " +
           "(:search IS NULL OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Proposal> findByFilters(
            @Param("freelancerId") Long freelancerId, 
            @Param("status") String status, 
            @Param("clienteId") Long clienteId, 
            @Param("search") String search, 
            Pageable pageable);

    // Métodos auxiliares para métricas
    long countByFreelancerIdAndStatus(Long freelancerId, String status);
    long countByFreelancerId(Long freelancerId);
}