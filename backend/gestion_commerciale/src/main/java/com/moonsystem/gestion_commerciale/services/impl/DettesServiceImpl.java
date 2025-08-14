package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.repository.BonsortiRepository;
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
    @Override
    public DettesResponseDto getDettesWithStats(int year, String mvt) {

        List<DettesDto> dettes = calculerTaux(bonSortiRepository.findByYearAndMvt(year, mvt));

        // Calculs globaux
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

        // Retourner l'objet global
        return DettesResponseDto.builder()
                .dettes(dettes)
                .totalCredits(totalCredits)
                .totalChiffre(totalChiffre)
                .tauxMoy(avgTaux)
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
}
