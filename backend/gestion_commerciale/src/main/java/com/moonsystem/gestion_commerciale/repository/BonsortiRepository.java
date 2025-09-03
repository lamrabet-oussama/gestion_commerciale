package com.moonsystem.gestion_commerciale.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.moonsystem.gestion_commerciale.dto.SommeTotauxDto;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.User;

public interface BonsortiRepository extends JpaRepository<Bonsorti, Integer> {

    Optional<Bonsorti> findByIdBon(Integer idBon);


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
            + "WHERE CAST(b.datBon AS date) = :date AND (:user IS NULL OR b.user =:user )")
    SommeTotauxDto sumTotauxByDate(@Param("date") LocalDate date,@Param("user") User user);

    // Pour les créances (CLIENT)
    @Query("""
SELECT new com.moonsystem.gestion_commerciale.dto.DettesDto(
    t.id,
    t.nom,
    COALESCE(SUM(b.espece), CAST(0 AS java.math.BigDecimal)) + COALESCE(SUM(b.cheque), CAST(0 AS java.math.BigDecimal)),
    COALESCE(t.solde, CAST(0 AS java.math.BigDecimal)),
    t.qualite,
    (SELECT MAX(r.datRegl) FROM Reglement r WHERE r.tier = t)
)
FROM Bonsorti b
JOIN b.tier t
WHERE b.datBon BETWEEN :startOfYear AND :endOfYear
    AND (t.qualite = 'CLIENT' OR t.qualite = 'MIXTE')
GROUP BY t.id, t.nom, t.qualite, t.solde
HAVING COALESCE(t.solde, CAST(0 AS java.math.BigDecimal)) > 0
ORDER BY t.solde DESC
""")
    List<DettesDto> findCreancesByYear(@Param("startOfYear") LocalDateTime startOfYear,@Param("endOfYear") LocalDateTime endOfYear);

    @Query("SELECT COALESCE(SUM(b.credit), 0) FROM Bonsorti b JOIN b.tier t  WHERE t.id =:tierId AND (:year IS NULL OR EXTRACT(YEAR FROM b.datBon) = :year) ")
    BigDecimal findTotalCreditByTierIdAndYear(@Param("tierId") Integer tierId,@Param("year") Integer year);

    // Pour les dettes (FOURNISSEUR)
    @Query("""
SELECT new com.moonsystem.gestion_commerciale.dto.DettesDto(
    t.id,
    t.nom,
    COALESCE(SUM(b.espece), CAST(0 AS java.math.BigDecimal)) + COALESCE(SUM(b.cheque), CAST(0 AS java.math.BigDecimal)),
    COALESCE(t.solde, CAST(0 AS java.math.BigDecimal)),
    t.qualite,
    (SELECT MAX(r.datRegl) FROM Reglement r WHERE r.tier = t)
)
FROM Bonsorti b
JOIN b.tier t
WHERE b.datBon BETWEEN :startOfYear AND :endOfYear
    AND (t.qualite = 'FOURNISSEUR' OR t.qualite = 'MIXTE')
GROUP BY t.id, t.nom, t.qualite, t.solde
HAVING COALESCE(t.solde, CAST(0 AS java.math.BigDecimal)) < 0
ORDER BY t.solde DESC
""")
    List<DettesDto> findDettesByYear(@Param("startOfYear") LocalDateTime startOfYear,
                                     @Param("endOfYear") LocalDateTime endOfYear);



    @Query("SELECT COUNT(b) FROM Bonsorti b WHERE  b.datBon BETWEEN :startDate AND :endDate AND b.mvt= :mvt")
    Long countByDateCreationBetween(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,String mvt);


    @Query(
            """
        SELECT b FROM Bonsorti b WHERE (:userCod IS NULL OR b.user.cod = :userCod) AND b.serie = :serie AND b.mvt = :mvt
"""
    )
    @EntityGraph(attributePaths = {"fluxes", "fluxes.article"})
   Bonsorti findBySerieAndMvtAndUserCod(String serie, MvtType mvt,Integer userCod);

    @Query("SELECT MAX(b.serie) FROM Bonsorti b WHERE b.datBon BETWEEN :start AND :end")
    String findMaxSerieForDay(@Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end
                            );

    @Query("""
SELECT b FROM Bonsorti b WHERE b.tier.id= :tierId
""")
    List<Bonsorti> findByTierId(@Param("tierId") Integer tierId);

    @Query("""
SELECT b FROM Bonsorti b WHERE b.tier.id= :tierId  AND (:year IS NULL OR EXTRACT(YEAR FROM b.datBon) = :year)
""")
    List<Bonsorti> findByTierIdAndYear(@Param("tierId") Integer tierId,@Param("year") Integer year);

    @Query("""
        SELECT b.serie FROM Bonsorti b WHERE (:userCod IS NULL OR b.user.cod = :userCod) AND b.mvt = :mvt ORDER BY b.datBon DESC
""")
    List<String> getAllByMvt(@Param("userCod") Integer userCod, @Param("mvt") MvtType mvt);

    Optional<Bonsorti> findBySerie(String serie);
}



