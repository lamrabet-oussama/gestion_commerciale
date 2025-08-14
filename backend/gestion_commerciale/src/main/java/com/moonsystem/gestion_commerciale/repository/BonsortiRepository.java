package com.moonsystem.gestion_commerciale.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
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
            + "SUM(b.montant), SUM(b.espece), SUM(b.cheque),SUM(b.credit)) "
            + "FROM Bonsorti b "
            + "WHERE CAST(b.datBon AS date) = :date")
    SommeTotauxDto sumTotauxByDate(@Param("date") LocalDate date);


    @Query("""
SELECT new com.moonsystem.gestion_commerciale.dto.DettesDto(
    t.id,
    t.nom,
    COALESCE(SUM(b.espece), 0) + COALESCE(SUM(b.cheque), 0),
    COALESCE(SUM(b.credit), 0),
    :mvt,
    (SELECT MAX(r.datRegl) FROM Reglement r WHERE r.tier = t)
)
FROM Bonsorti b
JOIN b.tier t
WHERE YEAR(b.datBon) = :year AND b.mvt = :mvt
GROUP BY t.id, t.nom
HAVING COALESCE(SUM(b.credit), 0) <> 0
ORDER BY SUM(b.credit) DESC
""")
    List<DettesDto> findByYearAndMvt(
            @Param("year") int year,
            @Param("mvt")  String mvt
    );


//    @Query("""
//    SELECT
//        SUM(b.credit) AS totalCredits,
//        SUM(b.montant) AS totalChiffre,
//        AVG(
//            CASE
//                WHEN (SUM(b.montant) + SUM(b.credit)) = 0 THEN 0
//                ELSE (SUM(b.credit) / (SUM(b.montant) + SUM(b.credit)) * 100)
//            END
//        ) AS avgTaux
//    FROM Bonsorti b
//    WHERE b.datBon BETWEEN :start AND :end
//      AND b.mvt = :mvt
//""")
//    Object[] findGlobalStats(LocalDateTime start, LocalDateTime end, String mvt);





    }
