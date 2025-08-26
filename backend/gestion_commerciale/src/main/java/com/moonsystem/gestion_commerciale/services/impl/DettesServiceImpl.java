package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.*;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.repository.BonsortiRepository;
import com.moonsystem.gestion_commerciale.repository.ReglementRepository;
import com.moonsystem.gestion_commerciale.repository.TierRepository;
import com.moonsystem.gestion_commerciale.services.DettesService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class DettesServiceImpl implements DettesService {
    private final BonsortiRepository bonSortiRepository;
    private final ReglementRepository reglementRepository;
    private final TierRepository tierRepository;

    public DettesResponseDto getCreances(int year) {

        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        List<DettesDto> dettes = calculerTaux(bonSortiRepository.findCreancesByYear(startOfYear,endOfYear));

        DettesTotalDto total= calculeGlobeaux( dettes);

        // Retourner l'objet global
        return DettesResponseDto.builder()
                .dettes(dettes)
                .totalCredits(total.getTotalCredits())
                .totalChiffre(total.getTotalChiffre())
                .tauxMoy(total.getAvgTaux())
                .build();
    }


    public DettesResponseDto getDettes(int year) {

        LocalDateTime startOfYear=LocalDateTime.of(year,1,1,0,0,0);
        LocalDateTime endOfYear=LocalDateTime.of(year, 12, 31, 23, 59, 59);
        List<DettesDto> dettes = calculerTaux(bonSortiRepository.findDettesByYear(startOfYear,endOfYear));

        DettesTotalDto total= calculeGlobeaux( dettes);

        // Retourner l'objet global
        return DettesResponseDto.builder()
                .dettes(dettes)
                .totalCredits(total.getTotalCredits())
                .totalChiffre(total.getTotalChiffre())
                .tauxMoy(total.getAvgTaux())
                .build();
    }

    private List<DettesDto> calculerTaux(List<DettesDto> dettes) {
        dettes.forEach(d -> {
            BigDecimal total = d.getChiffreAnnuelle().add(d.getDetteFinal());

            if (total.compareTo(BigDecimal.ZERO) == 0) {
                d.setTaux(BigDecimal.ZERO);
            } else {
                d.setTaux(
                        d.getDetteFinal()
                                .divide(total, 2, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                );
            }
        });
        return dettes;
    }

    private DettesTotalDto calculeGlobeaux(List<DettesDto> dettes){
        BigDecimal totalCredits = dettes.stream()
                .map(DettesDto::getDetteFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalChiffre = dettes.stream()
                .map(DettesDto::getChiffreAnnuelle)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgTaux = dettes.isEmpty() ? BigDecimal.ZERO :
                dettes.stream()
                        .map(DettesDto::getTaux)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(dettes.size()), 2, RoundingMode.HALF_UP);
        return DettesTotalDto.builder()
                .totalChiffre(totalChiffre)
                .totalCredits(totalCredits)
                .avgTaux(avgTaux).build();

    }

    @Override
    public DettesResponseDto getDettesCreances(int year,String qualite){
        if (qualite == null) {
            throw new IllegalArgumentException("Le type de qualité ne peut pas être null");
        }

        // On met la chaîne en majuscules pour comparer avec l'enum
        String qualiteUpper = qualite.toUpperCase();

        return switch (qualiteUpper) {
            case "CLIENT" -> getCreances(year);
            case "FOURNISSEUR" -> getDettes(year);
            default -> throw new InvalidOperationException(
                    "Type de qualité invalide. Les valeurs autorisées sont CLIENT ou FOURNISSEUR"
            );
        };
    }

    @Override
    public   List<BigDecimal> getTotalDettesByTierId(Integer tierId){

        Tier tier= this.tierRepository.findById(tierId).orElseThrow(
                ()-> new EntityNotFoundException("Tier non trouvé",List.of("Tier not found"),ErrorCodes.TIER_NOT_FOUND)
        );

        return List.of(tier.getSoldeFact(),tier.getSolde());

    }

    @Override
    public TierStatistiqueCreditDto getTierStatistiqueCreditByTierId(Integer tierId){
        Tier tier= this.tierRepository.findById(tierId).orElseThrow(
                ()->new EntityNotFoundException(
                        "Tier non trouvé",
                        List.of("Tier not found"),ErrorCodes.TIER_NOT_FOUND
                )
        );
        List<BonSortieDto> bons = this.bonSortiRepository.findByTierId(tierId).stream()
                .map(BonSortieDto::of).toList();

        List<ReglementDto> reglements = this.reglementRepository.findReglementByTierId(tierId).stream()
                .map(ReglementDto::toDto).toList();

        BigDecimal totalCredit = this.bonSortiRepository.findTotalCreditByTierId(tierId);
        BigDecimal totalDebit = this.reglementRepository.getTotalEspeceChequeByTier(tierId);
        BigDecimal percentageDebitCredit;
        if (totalCredit.compareTo(BigDecimal.ZERO) > 0) {
            percentageDebitCredit = totalDebit
                    .divide(totalCredit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            percentageDebitCredit = BigDecimal.ZERO; // ou autre logique
        }


        return
                TierStatistiqueCreditDto.builder()
                        .tierId(tierId)
                        .nomTier(tier.getNom())
                        .bonsorties(bons)
                        .reglements(reglements)
                        .totalCredit(totalCredit)
                        .totalDebit(totalDebit)
                        .percentageDebitCredit(percentageDebitCredit)
                        .build();

    }
}
