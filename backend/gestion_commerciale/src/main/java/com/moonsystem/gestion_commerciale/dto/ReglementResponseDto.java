package com.moonsystem.gestion_commerciale.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ReglementResponseDto {

    private List<ReglementDto> reglements;
    private BigDecimal totalEspece;
    private BigDecimal totalCheque;

}
