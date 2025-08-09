package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;

public class SommeTotauxDto {

    private BigDecimal totalMontant;
    private BigDecimal totalEspece;
    private BigDecimal totalCheque;

    public SommeTotauxDto(BigDecimal totalMontant, BigDecimal totalEspece, BigDecimal totalCheque) {
        this.totalMontant = totalMontant != null ? totalMontant : BigDecimal.ZERO;
        this.totalEspece = totalEspece != null ? totalEspece : BigDecimal.ZERO;
        this.totalCheque = totalCheque != null ? totalCheque : BigDecimal.ZERO;
    }

    public BigDecimal getTotalMontant() {
        return totalMontant;
    }

    public BigDecimal getTotalEspece() {
        return totalEspece;
    }

    public BigDecimal getTotalCheque() {
        return totalCheque;
    }
}
