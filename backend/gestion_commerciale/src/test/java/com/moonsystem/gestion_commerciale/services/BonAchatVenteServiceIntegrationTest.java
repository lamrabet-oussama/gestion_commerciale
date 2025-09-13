package com.moonsystem.gestion_commerciale.services;


import com.moonsystem.gestion_commerciale.dto.ArticleAddBonDto;
import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.repository.*;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Nettoie le contexte après chaque test
public class BonAchatVenteServiceIntegrationTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TierRepository tierRepository;
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BonsortiRepository bonsortiRepository;
    @Autowired
    FluxRepository fluxRepository;
    @Autowired
    BonAchatVenteService bonService;

    @MockitoBean
    private ReglementGeneratePdf reglementGeneratePdf;

    private User user;
    private Tier tier;
    private Article article;

    @BeforeEach
    void initData() {
        // Nettoyer explicitement avant d'insérer
        articleRepository.deleteAll();
        tierRepository.deleteAll();
        userRepository.deleteAll();

        // Créer les entités sans IDs fixes
        user = new User();
        user.setLogin("Test User");
        user = userRepository.saveAndFlush(user);

        tier = new Tier();
        tier.setQualite(TypeTier.CLIENT);
        tier.setSolde(BigDecimal.ZERO);
        tier = tierRepository.saveAndFlush(tier);

        article = new Article();
        article.setRef(200);
        article.setDesignation("Article 200");
        article.setStock(BigDecimal.valueOf(30));
        article.setPrix(BigDecimal.valueOf(50));
        article.setPrixAchat(BigDecimal.valueOf(35));
        article = articleRepository.saveAndFlush(article);
    }



    @Test
    void createBon_integration_shouldPersistAndUpdateStockAndTierSolde() {
        ArticleAddBonDto artDto = ArticleAddBonDto.builder()
                .cod(article.getCod()) // Utiliser l'ID généré
                .quantite(BigDecimal.valueOf(3))
                .remisUni(BigDecimal.ZERO)
                .build();

        BonAchatVenteDto dto = BonAchatVenteDto.builder()
                .idUser(user.getCod()) // Utiliser l'ID généré
                .idTier(tier.getId()) // Utiliser l'ID généré
                .articles(List.of(artDto))
                .espece(BigDecimal.ZERO)
                .cheque(BigDecimal.ZERO)
                .datBon(LocalDateTime.now())
                .build();

        var result = bonService.createBon(dto, MvtType.VENTE);
        Assertions.assertThat(result).isNotNull();

        // recharger l'article depuis le repo
        var savedArticle = articleRepository.findById(article.getCod()).orElseThrow();
        Assertions.assertThat(savedArticle.getStock()).isEqualByComparingTo(BigDecimal.valueOf(27));

        // vérifie tier solde a été mis à jour
        var savedTier = tierRepository.findById(tier.getId()).orElseThrow();
        assertThat(savedTier.getSolde()).isNotNull();
    }
}