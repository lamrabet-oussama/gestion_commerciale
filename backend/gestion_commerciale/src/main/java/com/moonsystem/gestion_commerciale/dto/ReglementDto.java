package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.Reglement;
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
public class ReglementDto {
    private Integer id;
    private Integer idTier;
    private Integer idUser;
    private String nomTier;
    private String nomUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal espece;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal cheque;
    private String detailsCheque;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDateTime datRegl;

    private BigDecimal reglement;
    private  BigDecimal totalEspece;
    private BigDecimal totalCheque;
    public static ReglementDto toDto(Reglement reg){
        return ReglementDto.builder()
                .id(reg.getIdRegl())
                .idTier(reg.getTier().getId())
                .idUser(reg.getUser().getCod())
                .nomUser(reg.getUser().getLogin())
                .nomTier(reg.getTier().getNom())
                .espece(reg.getEspece())
                .cheque(reg.getCheque())
                .reglement(reg.getTotal())
                .datRegl(reg.getDatRegl())
                .detailsCheque(reg.getDet_cheque())
                .build();
    }


}
