package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.Reglement;
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
    private List<BonSortieDto> bonsAchat;
    private List<BonSortieDto> bonsVente;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
// tous les bons du jour
    private BigDecimal totalMontant;
    // somme des montants (via SommeTotauxDto)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal totalEspece;
    // somme des espèces
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal totalCheque;       // somme des chèques

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal totalCredit;
    private String nomUser;
    private List<Reglement> reglements;
}
