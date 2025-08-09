package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseJourDto {

    private LocalDate date;               // jour considéré
    private List<BonSortieDto> bons;      // tous les bons du jour
    private BigDecimal totalMontant;      // somme des montants (via SommeTotauxDto)
    private BigDecimal totalEspece;       // somme des espèces
    private BigDecimal totalCheque;       // somme des chèques
    private String nomUser;
}
