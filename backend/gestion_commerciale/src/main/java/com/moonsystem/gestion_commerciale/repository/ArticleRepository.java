package com.moonsystem.gestion_commerciale.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moonsystem.gestion_commerciale.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query("SELECT CAST(COUNT(a) AS integer) FROM Article a")
    Integer countAll();

    Optional<Article> findByCod(Integer cod);

    Optional<Article> findByRef(Integer ref);

    Optional<Article> findByBarCode(String barCode);

    boolean existsByCod(Integer cod);

    @Query("SELECT DISTINCT a.famille FROM Article a")
    List<String> findDistinctFamilles();

    @Query("""
    SELECT a FROM Article a
            WHERE a.actif = true
        AND(
     LOWER(a.designation) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(a.choix) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR CAST(a.ref AS string) LIKE CONCAT('%', :keyword, '%')
    OR LOWER(a.famille) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Article> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT a.choix FROM Article a")
    List<String> findDistinctChoix();

    List<Article> findByDesignationContainingIgnoreCase(String designation);

    List<Article> findByFamilleIgnoreCase(String famille);

    List<Article> findByChoix(String choix);

    List<Article> findAllByOrderByDesignationAsc();

    List<Article> findByPrixBetween(BigDecimal min, BigDecimal max);

    List<Article> findByStockLessThan(BigDecimal stock);

    List<Article> findByStockGreaterThan(BigDecimal stock);

    List<Article> findByStockAlertGreaterThanEqual(BigDecimal stockAlert);

    List<Article> findByActifTrue();

    List<Article> findByActifFalse();

    List<Article> findByNoStockTrue();

    List<Article> findByFamilleAndActifTrue(String famille);

    List<Article> findByRefAndActifTrue(Integer ref);

    List<Article> findByDesignationContainingAndPrixLessThan(String designation, BigDecimal prixMax);

    List<Article> findAllByOrderByPrixDesc();

    List<Article> findAllByOrderByStockAsc();

    List<Article> findTop10ByOrderByStockAsc();

    List<Article> findTop5ByOrderByPrixDesc();

    @Query("SELECT a FROM Article a WHERE a.stock < a.stockAlert")
    List<Article> findArticlesWithLowStock();

    boolean existsByBarCode(String barCode);

    boolean existsByRef(Integer ref);

    Page<Article> findByActifTrue(Pageable pageable);

    Article findByDesignationAndChoix(String designation, String choix);

}
