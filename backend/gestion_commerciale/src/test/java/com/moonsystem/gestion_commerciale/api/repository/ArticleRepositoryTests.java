package com.moonsystem.gestion_commerciale.api.repository;

import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.repository.ArticleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")

public class ArticleRepositoryTests {

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    @Disabled("Désactivé temporairement car en cours de debug")

    public void ArticleRepo_SaveTest_Return_SavedArticle(){

        //Arrange
        Article articleSaved=Article.builder()
                .designation("Test Designation")
                .choix("Test choix")
                .stock(BigDecimal.ONE)
                .prix(BigDecimal.TEN)
                .prixAchat(BigDecimal.TEN)
                .ref(123)
                .famille("Test famille")
                .build();

        //Act
        articleRepository.save(articleSaved);

        //Assertions
        Assertions.assertThat(articleSaved).isNotNull();
        Assertions.assertThat(articleSaved.getCod()).isGreaterThan(0);

    }

    @Test
    @Disabled("Désactivé temporairement car en cours de debug")

    public void ArticleRepo_GetAll_ReturnAllArticles(){

        List<Article> articles=articleRepository.findAll();
        Assertions.assertThat(articles).isNotNull();
        Assertions.assertThat(articles.size()).isGreaterThan(0);
    }

    @Test
    @Disabled("Désactivé temporairement car en cours de debug")

    public void ArticleRepo_FindById_ReturnArticle(){
        Article article=articleRepository.findByCod(1).get();
        Assertions.assertThat(article).isNotNull();
    }

    @Test
    @Disabled("Désactivé temporairement car en cours de debug")

    void searchByKeyword_ReturnMatchingArticle(){
        Article a1=Article.builder()
                .designation("Clavier mécanique")
                .choix("Noir")
                .famille("Informatique")
                .ref(1007)
                .prix(BigDecimal.valueOf(100))
                .actif(true)
                .build();

        Article a2 = Article.builder()
                .designation("Souris sans fil")
                .choix("Gris")
                .famille("Accessoires")
                .ref(2008)
                .prix(BigDecimal.valueOf(50))
                .prixAchat(BigDecimal.valueOf(40))
                .actif(true)
                .build();

        Article a3 = Article.builder()
                .designation("Chaise de bureau")
                .choix("Rouge")
                .famille("Mobilier")
                .ref(3003)
                .prix(BigDecimal.valueOf(150))
                .prixAchat(BigDecimal.valueOf(120))
                .actif(false) // inactif
                .build();

        articleRepository.save(a1);
        articleRepository.save(a2);
        articleRepository.save(a3);
        List<Article> results = articleRepository.searchByKeyword("clavier");

        Assertions.assertThat(results)
                .isNotNull()
                .hasSize(1)
                .extracting(Article::getDesignation)
                .containsExactly("Clavier mécanique");
    }

    @Test
    @Disabled("Désactivé temporairement car en cours de debug")

    void findArticleByDesAndChoixAndActif_ReturnAllActifArticles(){
        Article a1=Article.builder()
                .designation("Clavier mécanique")
                .choix("Noir")
                .famille("Informatique")
                .ref(1007)
                .prix(BigDecimal.valueOf(100))
                .actif(true)
                .build();

        Article a2 = Article.builder()
                .designation("Souris sans fil")
                .choix("Gris")
                .famille("Accessoires")
                .ref(2008)
                .prix(BigDecimal.valueOf(50))
                .prixAchat(BigDecimal.valueOf(40))
                .actif(true)
                .build();

        Article a3 = Article.builder()
                .designation("Chaise de bureau")
                .choix("Rouge")
                .famille("Mobilier")
                .ref(3003)
                .prix(BigDecimal.valueOf(150))
                .prixAchat(BigDecimal.valueOf(120))
                .actif(false) // inactif
                .build();

        articleRepository.save(a1);
        articleRepository.save(a2);
        articleRepository.save(a3);

        Article a = articleRepository.findByDesignationAndChoixAndActif("Clavier mécanique","Noir",true);
        Assertions.assertThat(a).isNotNull();
    }
}
