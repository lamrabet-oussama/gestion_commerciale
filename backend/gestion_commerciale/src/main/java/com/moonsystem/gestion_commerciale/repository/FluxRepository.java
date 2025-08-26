package com.moonsystem.gestion_commerciale.repository;

import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.Flux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FluxRepository extends JpaRepository<Flux,Integer> {

    @Query("SELECT f FROM Flux f " +
            "WHERE f.article.cod = :articleId " +
            "AND f.dateFlux BETWEEN :startOfYear AND :endOfYear")
    List<Flux> findByArticleIdAndYear(@Param("articleId") Integer articleId,
                                      @Param("startOfYear") LocalDateTime startOfYear,@Param("endOfYear") LocalDateTime endOfYear);

    @Query("SELECT f FROM Flux f " +
            "WHERE f.article.cod = :articleId " +
            "AND YEAR(f.dateFlux) = :year " +
            "ORDER BY f.dateFlux DESC")
    Flux findTopByArticleIdAndYearOrderByDateFluxDesc(
            @Param("articleId") Integer articleId,
            @Param("year") Integer year
    );

    @Query("""
SELECT COALESCE(SUM(f.entree),0) - COALESCE(SUM(f.sortie),0) as stockInitial,
       :startDate as dateReference
FROM Flux f
WHERE f.article.cod = :articleId
AND f.dateFlux < :startDate
""")
    Object getStockInitialWithDate(@Param("articleId") Integer articleId,
                                   @Param("startDate") LocalDateTime startDate);



    @Query("""
SELECT SUM(f.entree)  as totalEntres,SUM(f.sortie) as totalSortie
       
FROM Flux f
WHERE f.article.cod = :articleId
AND YEAR(f.dateFlux) = :year
""")
    Object getTotalEntresSorties(@Param("articleId") Integer articleId,
                                 @Param("year") Integer year);

    List<Flux> findByBonSorti(Bonsorti bonSorti);
}




