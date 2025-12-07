package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    Page<Template> findByFreelancerId(Long freelancerId, Pageable pageable);
    Page<Template> findByFreelancerIdAndNomeContainingIgnoreCase(Long freelancerId, String nome, Pageable pageable);
}