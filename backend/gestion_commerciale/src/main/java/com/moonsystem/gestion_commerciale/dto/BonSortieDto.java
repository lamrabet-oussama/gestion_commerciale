package com.moonsystem.gestion_commerciale.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.validator.ValidEnum;
import jakarta.validation.constraints.NotNull;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal montant;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal espece;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal cheque;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal credit;
    private String detCheque;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")

    private LocalDateTime datBon;
    private String nomTier;
    private String nomUser;
    @NotNull(message = "Le mouvement est requise")
    @ValidEnum(enumClass = MvtType.class, message = "Type de Mouvement invalide")
    private String mvt;

    public static BonSortieDto of(com.moonsystem.gestion_commerciale.model.Bonsorti entity) {
        return BonSortieDto.builder()
                .idBon(entity.getIdBon())
                .serie(entity.getSerie())
                .montant(entity.getMontant())
                .espece(entity.getEspece())
                .cheque(entity.getCheque())
                .detCheque(entity.getDetCheq())
                .credit(entity.getCredit())
                .datBon(entity.getDatBon())
                .mvt(entity.getMvt().name())
                .nomTier(entity.getTier() != null ? entity.getTier().getNom() : null)
                .nomUser(entity.getUser() != null ? entity.getUser().getLogin() : null)
                .build();
    }


}
