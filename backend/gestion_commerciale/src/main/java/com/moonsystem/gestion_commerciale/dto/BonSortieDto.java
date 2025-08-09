package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonSortieDto {

    private Integer idBon;
    private String serie;
    private BigDecimal montant;
    private BigDecimal espece;
    private BigDecimal cheque;
    private BigDecimal credit;
    private LocalDateTime datBon;
    private String nomTier;
    private String nomUser;

    public static BonSortieDto of(com.moonsystem.gestion_commerciale.model.Bonsorti entity) {
        return BonSortieDto.builder()
                .idBon(entity.getIdBon())
                .serie(entity.getSerie())
                .montant(entity.getMontant())
                .espece(entity.getEspece())
                .cheque(entity.getCheque())
                .credit(entity.getCredit())
                .datBon(entity.getDatBon())
                .nomTier(entity.getTier() != null ? entity.getTier().getNom() : null)
                .nomUser(entity.getUser() != null ? entity.getUser().getLogin() : null)
                .build();
    }
}
