package com.freelaflow.back_freelaflow.repository;

import com.freelaflow.back_freelaflow.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
    SELECT c FROM Category c
    WHERE (:ativo IS NULL OR c.ativo = :ativo)
    AND (:search IS NULL OR LOWER(c.tipo) LIKE LOWER(CONCAT('%', :search, '%')))
    AND c.freelancer.id = :freelancer_id
""")
    Page<Category> findByFreelancerAndTipoOrAtivo(
            @Param("freelancer_id") Long freelancer_id,
            @Param("search") String search,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );

    boolean existsByTipoAndFreelancerId(String tipo, Long freelancerId);
}
