package com.moonsystem.gestion_commerciale.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moonsystem.gestion_commerciale.model.MesInfox;

import java.util.Optional;

@Repository
public interface MesInfoxRepository extends JpaRepository<MesInfox, Integer> {

    @Query("SELECT m FROM MesInfox m")
    Optional<MesInfox> findFirstInfo();

    Optional<MesInfox> findByNum(Integer num);
}
