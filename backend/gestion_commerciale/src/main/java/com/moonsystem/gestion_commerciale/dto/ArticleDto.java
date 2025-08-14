package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moonsystem.gestion_commerciale.model.Article;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
public class ArticleDto {

    private Integer cod;
    private Integer ref;
    private String designation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")

    private BigDecimal prix;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")

    private BigDecimal prixMin;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")

    private BigDecimal prixAchat;
    private BigDecimal tauxTva;
    private String note;
    private String choix;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")

    private BigDecimal stock;
//    @NotBlank(message = "La famille est obligatoire")
    private String famille;

    // Convertir un Article (Entity) en ArticleDto avec gestion des valeurs null
    public static ArticleDto fromEntity(Article article) {
        if (article == null) {
            return null;
        }

        return ArticleDto.builder()
                .cod(article.getCod())
                .ref(article.getRef())
                .designation(Optional.ofNullable(article.getDesignation()).orElse(""))
                .prix(Optional.ofNullable(article.getPrix()).orElse(BigDecimal.ZERO))
                .prixMin(Optional.ofNullable(article.getPrixMin()).orElse(BigDecimal.ZERO))
                .prixAchat(article.getPrixAchat())
                .tauxTva(Optional.ofNullable(article.getTauxTva()).orElse(BigDecimal.ZERO))
                .note(Optional.ofNullable(article.getNote()).orElse(""))
                .stock(Optional.ofNullable(article.getStock()).orElse(BigDecimal.ZERO))
                .famille(Optional.ofNullable(article.getFamille()).orElse(""))
                .choix(Optional.ofNullable(article.getChoix()).orElse(""))
                .build();
    }

    // Convertir un ArticleDto en Article (Entity) avec gestion des valeurs null
    public static Article toEntity(ArticleDto dto) {
        if (dto == null) {
            return null;
        }

        Article article = new Article();
        article.setCod(dto.getCod());
        article.setRef(dto.getRef());
        article.setDesignation(dto.getDesignation());
        article.setPrix(dto.getPrix());
        article.setStock(dto.getStock());
        article.setFamille(dto.getFamille());
        article.setPrixAchat(dto.getPrixAchat());
        article.setPrixMin(dto.getPrixMin());
        article.setTauxTva(dto.getTauxTva());
        article.setNote(dto.getNote());
        article.setChoix(dto.getChoix());
        return article;
    }
}
