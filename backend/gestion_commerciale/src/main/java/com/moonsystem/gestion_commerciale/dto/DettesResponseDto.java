package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DettesResponseDto {
    private List<DettesDto> dettes;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalCredits;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalChiffre;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal tauxMoy;
}
