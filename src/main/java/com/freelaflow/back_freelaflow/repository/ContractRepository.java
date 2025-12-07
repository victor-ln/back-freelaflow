package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Conta contratos ativos
    long countByFreelancerIdAndStatus(Long freelancerId, String status);

    // Soma o valor dos serviços vinculados aos contratos ativos do freelancer
    // O COALESCE garante que retorne 0.0 em vez de NULL se não houver contratos
    @Query("SELECT COALESCE(SUM(s.valor), 0) FROM Contract c JOIN c.service s WHERE c.freelancer.id = :freelancerId AND c.status = :status")
    Double sumValorTotalByFreelancerIdAndStatus(@Param("freelancerId") Long freelancerId, @Param("status") String status);
}
