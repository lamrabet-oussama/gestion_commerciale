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
    WHERE (:tier IS NULL OR r.tier = :tier)
      AND (:user IS NULL OR r.user = :user)
      AND  r.datRegl BETWEEN :start AND :end
    ORDER BY r.datRegl DESC
""")
    List<Reglement> findByTierAndUserAndDateReglements(
            @Param("tier") Tier tier,
            @Param("user") User user,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
    SELECT r FROM Reglement r
    WHERE (:tier IS NULL OR r.tier = :tier)
      AND (:user IS NULL OR r.user = :user)
      AND (:year IS NULL OR EXTRACT(YEAR FROM r.datRegl) = :year)
    ORDER BY r.datRegl DESC
  """)
    List<Reglement> findByTierAndUserAndYearReglements(
            @Param("tier") Tier tier,
            @Param("user") User user,
            @Param("year") Integer year
    );

    @Query("SELECT COALESCE(SUM(r.espece+ r.cheque), 0) FROM Reglement r WHERE r.tier.id =:tierId AND (:year IS NULL OR EXTRACT(YEAR FROM r.datRegl) = :year)")
    BigDecimal getTotalEspeceChequeByTierAndYear(@Param("tierId") Integer tierId,@Param("year") Integer year);

    @Query("""
    SELECT r FROM Reglement r WHERE r.tier.id = :tierId
""")
    List<Reglement> findReglementByTierId(@Param("tierId") Integer tierId);

    @Query("""
    SELECT r FROM Reglement r WHERE r.tier.id = :tierId AND (:year IS NULL OR EXTRACT(YEAR FROM r.datRegl) = :year)
""")
    List<Reglement> findReglementByTierIdAndYear(@Param("tierId") Integer tierId,@Param("year") Integer year);

    Optional<Reglement> findReglementByIdRegl(Integer id);

    @Query("""
    SELECT r FROM Reglement r
    WHERE (:user IS NULL OR r.user = :user)
      AND (:dateDebut IS NULL OR r.datRegl >= :dateDebut)
      AND (:dateFin IS NULL OR r.datRegl <= :dateFin)
      
    ORDER BY r.datRegl DESC
    
""")
    List<Reglement> findByDate(LocalDateTime dateDebut, LocalDateTime dateFin,User user);
}
