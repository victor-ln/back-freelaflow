package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Page<Service> findByFreelancerIdAndAtivoTrue(Long freelancerId, Pageable pageable);
    Page<Service> findByFreelancerIdAndNomeContainingIgnoreCaseAndAtivoTrue(Long freelancerId, String nome, Pageable pageable);
}