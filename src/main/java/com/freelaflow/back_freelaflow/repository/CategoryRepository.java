package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByTipoAndFreelancerId(String tipo, Long freelancerId);
}