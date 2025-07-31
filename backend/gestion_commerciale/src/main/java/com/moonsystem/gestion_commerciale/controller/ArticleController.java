package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.ArticleApi;
import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.services.ArticleService;
import com.moonsystem.gestion_commerciale.utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Tag(name = "Articles", description = "API de gestion des articles")

public class ArticleController implements ArticleApi {
    private ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public ArticleDto save(ArticleDto dto) {
        return articleService.save(dto);
    }

    @Override
    public ArticleDto findByCod(Integer code) {
        return articleService.findByCod(code);
    }

    @Override
    public List<ArticleDto> findAll() {
        return articleService.findAll();
    }

    @Override
    public ArticleDto update(Integer code, ArticleDto dto) {
        return articleService.update(code, dto);
    }

    @Override
    public boolean delete(Integer code) {
return articleService.delete(code);
    }
    @Override
    public List<String> getFamilles() {
        return articleService.findDistinctFamilles();
    }
    @Override
    public List<String> getChoix() {
        return articleService.findDistinctChoix();
    }
    @Override
    public List<ArticleDto> searchArticles( String keyword) {
        return articleService.search(keyword);
    }
    @Override
    public ArticleDto updateStock( Integer cod, BigDecimal newStock) {
        return articleService.updateStock(cod, newStock);
    }

    @Override
    public PageResponse<ArticleDto> getArticlesPaginated(int page, int size) {
        return articleService.findAllPaginated(page,size);
    }
}
