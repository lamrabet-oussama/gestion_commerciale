package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.User;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.repository.*;
import com.moonsystem.gestion_commerciale.services.impl.BonAchatVenteServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BoenVenteServiceTest {
    @InjectMocks
    BonAchatVenteServiceImpl service;
    @Mock
    UserRepository userRepository;
    @Mock
    BonsortiRepository bonsortiRepository;
    @Mock
    TierRepository tierRepository;
    @Mock
    ArticleRepository articleRepository;
    @Mock
    FluxRepository fluxRepository;
    @Mock
    BonGeneratePdf pdfGenerator;

    private User user;
    private Tier tier;
    private Article article1;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setCod(1);

        tier = new Tier();
        tier.setId(10);
        tier.setQualite(TypeTier.CLIENT);
        tier.setSolde(BigDecimal.ZERO);

        article1 = new Article();
        article1.setCod(100);
        article1.setRef(100);
        article1.setDesignation("Article 100");
        article1.setStock(BigDecimal.valueOf(50));
        article1.setPrix(BigDecimal.valueOf(20));
        article1.setPrixAchat(BigDecimal.valueOf(15));
        article1.setPrixMin(BigDecimal.valueOf(10));
    }
    @Test
    void createBon_shouldThrow_whenArticlesEmpty() {
        BonAchatVenteDto dto = new BonAchatVenteDto();
        dto.setArticles(List.of()); // vide

        InvalidOperationException ex = Assert.assertThrows(InvalidOperationException.class,
                () -> service.createBon(dto, MvtType.VENTE));
        Assert.assertTrue(ex.getMessage().contains("Aucun Article"));
    }
}
