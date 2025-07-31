package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.utils.PageResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ArticleService {
    ArticleDto save(ArticleDto dto);
    ArticleDto findByCod(Integer code);
    List<ArticleDto> findAll();
    ArticleDto update( Integer cod,ArticleDto dto);
    boolean delete(Integer code);
    List<String> findDistinctFamilles();
    List<String> findDistinctChoix();
    List<ArticleDto> search(String keyword);
    ArticleDto updateStock(Integer cod, BigDecimal newStock);
    public PageResponse<ArticleDto> findAllPaginated(int page, int size);
    public void checkUnicityDesignationAndChoix(ArticleDto dto);

}
