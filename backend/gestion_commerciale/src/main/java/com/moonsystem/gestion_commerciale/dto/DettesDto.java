package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DettesDto {
    private Integer idTier;
   private String nomTier;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
   private BigDecimal chiffreAnnuelle;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")

    private BigDecimal detteFinal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal taux;
    private String mvt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime dateRegl;

    public DettesDto(Integer idTier,String nomTier,BigDecimal chiffreAnnuelle,BigDecimal detteFinal,String mvt,LocalDateTime dateRegl) {
        this.idTier = idTier;
        this.nomTier = nomTier;
        this.chiffreAnnuelle = chiffreAnnuelle;
        this.detteFinal = detteFinal;
        this.mvt = mvt;
        this.dateRegl = dateRegl;
    }

}
