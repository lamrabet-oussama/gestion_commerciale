package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
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
public class TierStatistiqueCreditDto {

    private Integer tierId;
    private String nomTier;
   // private String qualiteTier;
    private List<BonSortieDto> bonsorties;
    private List<ReglementDto> reglements;
    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalCredit;

    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal totalDebit;

    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern = "0.00")
    BigDecimal percentageDebitCredit;


}
