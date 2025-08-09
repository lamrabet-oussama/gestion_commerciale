package com.moonsystem.gestion_commerciale.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moonsystem.gestion_commerciale.dto.SommeTotauxDto;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.User;

public interface BonsortiRepository extends JpaRepository<Bonsorti, Integer> {

    Optional<Bonsorti> findByIdBon(Integer idBon);

    Bonsorti findBySerie(String serie);

    List<Bonsorti> findByDatBonBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Bonsorti> findByUser(User user);

    @Query("SELECT b FROM Bonsorti b WHERE b.montant > :amount")
    List<Bonsorti> findByMontantGreaterThan(@Param("amount") BigDecimal amount);

    @Query("""
    SELECT b FROM Bonsorti b
    WHERE (:user IS NULL OR b.user = :user)
      AND (:startDate IS NULL OR b.datBon >= :startDate)
      AND (:endDate IS NULL OR b.datBon <= :endDate)
    ORDER BY b.datBon DESC
""")
    List<Bonsorti> findByFilters(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT new com.moonsystem.gestion_commerciale.dto.SommeTotauxDto("
            + "SUM(b.montant), SUM(b.espece), SUM(b.cheque)) "
            + "FROM Bonsorti b "
            + "WHERE CAST(b.datBon AS date) = :date")
    SommeTotauxDto sumTotauxByDate(@Param("date") LocalDate date);

}
