package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SommeTotauxDto {
    private BigDecimal totalMontant;

    private BigDecimal totalEspece;

    private BigDecimal totalCheque;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal totalCredit;
    public SommeTotauxDto(BigDecimal totalMontant, BigDecimal totalEspece, BigDecimal totalCheque,BigDecimal totalCredit) {
        this.totalMontant = totalMontant != null ? totalMontant : BigDecimal.ZERO;
        this.totalEspece = totalEspece != null ? totalEspece : BigDecimal.ZERO;
        this.totalCheque = totalCheque != null ? totalCheque : BigDecimal.ZERO;
        this.totalCredit = totalCredit != null ? totalCredit : BigDecimal.ZERO;

    }


}
