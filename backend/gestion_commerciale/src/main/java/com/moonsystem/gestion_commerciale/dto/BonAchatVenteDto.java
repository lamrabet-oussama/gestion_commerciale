package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonAchatVenteDto {
    private Integer idBon;
    private String serie;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal montant;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")  // **AJOUT du format**
    private BigDecimal montantSansRemise;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal espece;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal cheque=BigDecimal.ZERO;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal credit;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal remis;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal remisSurBon;
    private String detCheque="........";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datBon;
    private Integer idTier;
    private String nomTier;
    private Integer idUser;
    private String nomUser;
    private String mvt;
    List<ArticleAddBonDto> articles;

    public static BonAchatVenteDto mapToDto(Bonsorti bon, List<ArticleAddBonDto> articlesDto) {
        BonAchatVenteDto dto = new BonAchatVenteDto();
        dto.setIdBon(bon.getIdBon());
        dto.setSerie(bon.getSerie());
        dto.setMvt(bon.getMvt().name());

        // **CORRECTION** : montant du Bonsorti = montantSansRemise
        dto.setMontantSansRemise(bon.getMontant());

        // **CALCUL** : montant final = montantSansRemise - remis
        BigDecimal montantFinal = bon.getMontant();
        if (bon.getRemis() != null) {
            montantFinal = montantFinal.subtract(bon.getRemis());
        }
        dto.setMontant(montantFinal);

        dto.setEspece(bon.getEspece());
        dto.setCheque(bon.getCheque());
        dto.setCredit(bon.getCredit());
        dto.setDetCheque(bon.getDetCheq());
        dto.setDatBon(bon.getDatBon());
        dto.setRemis(bon.getRemis());
        dto.setIdTier(bon.getTier().getId());
        dto.setNomTier(bon.getTier().getNom());
        dto.setDetCheque(bon.getDetCheq());
        dto.setIdUser(bon.getUser().getCod());
        dto.setNomUser(bon.getUser().getLogin());
    dto.setRemisSurBon(bon.getRemisSurBon()!=null ? bon.getRemisSurBon():BigDecimal.ZERO);
        // **MODIFICATION** : recevoir la liste d'articles depuis le service
        dto.setArticles(articlesDto);

        return dto;
    }
}