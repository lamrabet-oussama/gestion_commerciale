package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.FluxNormalDto;
import com.moonsystem.gestion_commerciale.dto.FluxNormalResponseDto;
import com.moonsystem.gestion_commerciale.dto.StockInitialDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.model.Flux;
import com.moonsystem.gestion_commerciale.repository.ArticleRepository;
import com.moonsystem.gestion_commerciale.repository.FluxRepository;
import com.moonsystem.gestion_commerciale.services.FluxNormalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FluxNormalServiceImp implements FluxNormalService {

    private final FluxRepository fluxRepository;
    private final ArticleRepository articleRepository;

   public List<FluxNormalDto> getFluxNormalArticle(Integer articleId,Integer year){
       LocalDateTime startOfYear = LocalDateTime.of(year,1,1,1,1);
       LocalDateTime endOfYear = LocalDateTime.of(year,12,31,23,59,59);
        List<Flux> fluxes=this.fluxRepository.findByArticleIdAndYear(articleId,startOfYear,endOfYear);
       return fluxes.stream().map(
                FluxNormalDto::fromEntity
        ).toList();
    }
    public StockInitialDto getStockInitialAndDate(Integer articleId, Integer currentYear) {
        // Début de l'année courante
        LocalDateTime startDate = LocalDateTime.of(currentYear, 1, 1, 0, 0);

        // Appel au repository
        Object result = fluxRepository.getStockInitialWithDate(articleId, startDate);

        Object[] row = (Object[]) result;
        BigDecimal stockInitial = BigDecimal.valueOf(((Number) row[0]).doubleValue());
        LocalDateTime dateReference = (LocalDateTime) row[1];

        return new StockInitialDto(stockInitial, dateReference);
    }


    @Override
    public FluxNormalResponseDto getFluxNormalResponseArticle(Integer articleId, Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        Optional<Article> article = articleRepository.findByCod(articleId);
        if (article.isEmpty()) {
            return FluxNormalResponseDto.builder()
                    .fluxes(Collections.emptyList()) // liste vide
                    .stockInitial(BigDecimal.ZERO)   // stock initial à 0
                    .build();
        }

        List<FluxNormalDto> fluxes = getFluxNormalArticle(articleId, year);
        StockInitialDto stockInitialAndDate = getStockInitialAndDate(articleId,year);
        Object totaux=fluxRepository.getTotalEntresSorties(articleId,year);
        Object[] row = (Object[]) totaux;

        BigDecimal totalEntrees = row[0] != null ? BigDecimal.valueOf(((Number) row[0]).doubleValue()) : BigDecimal.ZERO;
        BigDecimal totalSorties = row[1] != null ? BigDecimal.valueOf(((Number) row[1]).doubleValue()) : BigDecimal.ZERO;
        return FluxNormalResponseDto.builder()
                .fluxes(fluxes)
                .stockInitial(stockInitialAndDate.getStockInitial())
                .dateInitial(stockInitialAndDate.getDateReference())
                .totalEntres(totalEntrees)
                .totalSorties(totalSorties)
                .build();
    }

}
