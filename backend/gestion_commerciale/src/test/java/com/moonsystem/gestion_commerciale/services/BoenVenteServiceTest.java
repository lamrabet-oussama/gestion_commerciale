package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.ArticleAddBonDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void createBon_shouldSaveBonAndUpdateStock_whenVenteValid() {
        // Arrange DTO
        ArticleAddBonDto artDto = ArticleAddBonDto.builder()
                .cod(article1.getCod())
                .ref(article1.getRef())
                .quantite(BigDecimal.valueOf(5))
                .remisUni(BigDecimal.ZERO)
                .build();

        BonAchatVenteDto dto = BonAchatVenteDto.builder()
                .idUser(user.getCod())
                .idTier(tier.getId())
                .articles(List.of(artDto))
                .espece(BigDecimal.ZERO)
                .cheque(BigDecimal.ZERO)
                .datBon(LocalDateTime.now())
                .build();

        // Mocks
        when(userRepository.findByCod(user.getCod())).thenReturn(Optional.of(user));
        when(tierRepository.findById(tier.getId())).thenReturn(Optional.of(tier));
        when(articleRepository.findAllById(List.of(article1.getCod()))).thenReturn(List.of(article1));
        when(bonsortiRepository.save(any())).thenAnswer(inv -> inv.getArgument(0)); // retourne l'entité
        when(fluxRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var resultDto = service.createBon(dto, MvtType.VENTE);

        // Assert
        Assert.assertNotNull(resultDto);
        // Vérifier que stock a diminué de 5
        Assert.assertEquals(BigDecimal.valueOf(45), article1.getStock());
        verify(articleRepository, atLeastOnce()).save(article1);
        verify(bonsortiRepository).save(any());
        verify(fluxRepository).saveAll(any());
        verify(tierRepository).saveAndFlush(tier);
    }

    @Test
    void createBon_shouldThrow_whenStockInsufficient_forVente() {
        ArticleAddBonDto artDto = ArticleAddBonDto.builder()
                .cod(article1.getCod())
                .quantite(BigDecimal.valueOf(500)) // plus que stock
                .build();

        BonAchatVenteDto dto = BonAchatVenteDto.builder()
                .idUser(user.getCod())
                .idTier(tier.getId())
                .articles(List.of(artDto))
                .build();

        when(userRepository.findByCod(user.getCod())).thenReturn(Optional.of(user));
        when(tierRepository.findById(tier.getId())).thenReturn(Optional.of(tier));
        when(articleRepository.findAllById(List.of(article1.getCod()))).thenReturn(List.of(article1));

        InvalidOperationException ex = Assert.assertThrows(InvalidOperationException.class,
                () -> service.createBon(dto, MvtType.VENTE));
        Assert.assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }
}
