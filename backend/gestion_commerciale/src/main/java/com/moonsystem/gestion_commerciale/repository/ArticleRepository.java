package com.moonsystem.gestion_commerciale.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import com.moonsystem.gestion_commerciale.model.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query("SELECT CAST(COUNT(a) AS integer) FROM Article a")
    Integer countAll();

    @Query("""
    SELECT a FROM Article a WHERE a.actif=true ORDER BY a.ref
""")
    List<Article> findAllArticles();


    Optional<Article> findByCod(Integer cod);




    @Query("SELECT DISTINCT a.famille FROM Article a WHERE a.actif=true")
    List<String> findDistinctFamilles();

    @Query("SELECT DISTINCT a.designation FROM Article a WHERE a.actif=true")
    List<String> findDistinctDesignation();

    @Query("SELECT a.choix FROM Article a WHERE a.designation= :designation AND a.actif=true")
    List<String> findChoixByDesignation(String designation);

    @Query("""
    SELECT a FROM Article a
            WHERE a.actif = true
        AND(
     LOWER(a.designation) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(a.choix) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR CAST(a.ref AS string) LIKE CONCAT('%', :keyword, '%')
    OR LOWER(a.famille) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR CAST(a.prix As string) LIKE CONCAT('%',:keyword,'%')
            )
    """)
    List<Article> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT a.choix FROM Article a")
    List<String> findDistinctChoix();




     Article findByDesignationAndChoixAndActif(String designation, String choix, boolean actif);



    boolean existsByRef(Integer ref);

    Page<Article> findByActifTrueOrderByRefAsc(Pageable pageable);

    @Query("""
SELECT a FROM Article a 
WHERE LOWER(a.designation) = LOWER(:designation) 
AND LOWER(a.choix) = LOWER(:choix) 
AND a.actif = true
ORDER BY a.ref
""")
    Article findByDesignationAndChoix(String designation, String choix);


}
