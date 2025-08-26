package com.moonsystem.gestion_commerciale.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moonsystem.gestion_commerciale.model.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAddBonDto {
    private Integer cod;
    private Integer ref;
    private String designation;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal prix;
    private BigDecimal quantite;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "0.00")
    private BigDecimal remisUni;


    public static ArticleAddBonDto toDto(Article article) {
        if (article == null) {
            return null;
        }
        return ArticleAddBonDto.builder()
                .cod(article.getCod())
                .ref(article.getRef())
                .designation(article.getDesignation())
                .prix(article.getPrix())
                .build();
    }

}
