package com.moonsystem.gestion_commerciale.repository;

import com.moonsystem.gestion_commerciale.dto.SommeTotauxDto;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.Reglement;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReglementRepository extends JpaRepository<Reglement, Integer> {

    @Query("""
    SELECT r FROM Reglement r
    WHERE (:user IS NULL OR r.user.cod = :iduser)
      AND (:startDate IS NULL OR r.datRegl >= :startDate)
      AND (:endDate IS NULL OR r.datRegl <= :endDate)
      
    ORDER BY r.datRegl DESC
    
""")

    List<Reglement> findByDateAndUser(
            @Param("iduser") Integer idUser,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT r FROM Reglement r
    WHERE (:tier IS NULL OR r.tier.id = :idtier)
      AND (:startDate IS NULL OR r.datRegl >= :startDate)
      AND (:endDate IS NULL OR r.datRegl <= :endDate)
      
    ORDER BY r.datRegl DESC
    
""")


    List<Reglement> findByDateAndTier(
            @Param("idtier") Integer idTier,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
    SELECT r FROM Reglement r
    WHERE (:idtier IS NULL OR r.tier.id = :idtier)
    AND (:iduser IS NULL OR r.user.cod= :iduser)
      
    ORDER BY r.datRegl DESC
    
""")

    List<Reglement> findByTierAndUser(
            @Param("idtier") Integer idTier,
            @Param("iduser") Integer idUser

            );

    @Query("SELECT COALESCE(SUM(r.espece+ r.cheque), 0) FROM Reglement r WHERE r.tier.id =:tierId")
    BigDecimal getTotalEspeceChequeByTier(@Param("tierId") Integer tierId);

    @Query("""
    SELECT r FROM Reglement r WHERE r.tier.id = :tierId
""")
    List<Reglement> findReglementByTierId(@Param("tierId") Integer tierId);

    Optional<Reglement> findReglementByIdRegl(Integer id);
}
