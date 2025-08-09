package com.moonsystem.gestion_commerciale.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moonsystem.gestion_commerciale.model.Tier;

@Repository
public interface TierRepository extends JpaRepository<Tier, Integer> {

    Optional<Tier> findByRef(Integer ref);

    long count();

    @Query("""
       SELECT t FROM Tier t
       WHERE (LOWER(t.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.type) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(t.qualite) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR CAST(t.ref AS string) LIKE CONCAT('%', :keyword, '%'))
          AND t.actif = true
       """)

    Page<Tier> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByRef(Integer ref);

    boolean existsByNom(String name);
}
