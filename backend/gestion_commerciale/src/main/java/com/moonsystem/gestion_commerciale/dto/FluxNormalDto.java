package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.model.Flux;
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
public class FluxNormalDto {

    private Integer idFlux;
    private String libelle;

    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern="0.00")
    private Integer achat;

    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern="0.00")
    private Integer vente;

    @JsonFormat(shape =  JsonFormat.Shape.STRING,pattern="0.00")
    private BigDecimal stock;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")

    private LocalDateTime dateFlux;

    private BigDecimal fRemis;
    private Integer articleId;

    public static FluxNormalDto fromEntity(Flux flux){
        return FluxNormalDto.builder()
                .idFlux(flux.getIdFlux())
                .articleId( flux.getArticle()!=null ? flux.getArticle().getCod():null)
                .libelle(flux.getLibelle())
                .stock(flux.getArticle()!=null ? flux.getArticle().getStock():null)
                .achat(flux.getEntree())
                .dateFlux(flux.getDateFlux())
                .fRemis(flux.getFRemis())
                .vente(flux.getSortie()).build();
    }

    public static Flux toEntity(FluxNormalDto dto) {
        Flux flux = new Flux();
        flux.setIdFlux(dto.getIdFlux());
        flux.setLibelle(dto.getLibelle());
        flux.setEntree(dto.getAchat());
        flux.setSortie(dto.getVente());
        flux.setDateFlux(dto.getDateFlux());

        if (dto.getArticleId() != null) {
            Article article = new Article();
            article.setCod(dto.getArticleId());
            flux.setArticle(article);
        }

        return flux;
    }

}
