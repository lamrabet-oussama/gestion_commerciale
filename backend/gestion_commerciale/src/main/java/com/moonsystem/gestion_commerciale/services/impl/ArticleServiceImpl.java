package com.moonsystem.gestion_commerciale.services.impl;

import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import com.moonsystem.gestion_commerciale.exception.InvalidOperationException;
import com.moonsystem.gestion_commerciale.model.Article;
import com.moonsystem.gestion_commerciale.repository.ArticleRepository;
import com.moonsystem.gestion_commerciale.services.ArticleService;
import com.moonsystem.gestion_commerciale.utils.PageResponse;
import com.moonsystem.gestion_commerciale.validator.ArticleValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }



    // Méthode privée centrale pour récupérer un article actif ou lancer une exception
    private Article getActiveArticleEntityByCod(Integer cod) {
        if (cod == null) {
            log.error("Code article est null");
            throw new InvalidEntityException("Le code de l'article est obligatoire", ErrorCodes.ARTICLE_NOT_VALID);
        }

        Article article = articleRepository.findByCod(cod)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun article avec le code " + cod + " n'a été trouvé"
                ));

        if (Boolean.FALSE.equals(article.getActif())) {
            throw new InvalidOperationException(
                    "L'article avec le code " + cod + " est inactif",
                    ErrorCodes.ARTICLE_NOT_FOUND
            );
        }
        return article;
    }
    public void checkUnicityDesignationAndChoix(ArticleDto dto) {
        if (dto.getCod() != null) {
            // update : on doit vérifier que l'article existe
            Article existe = getActiveArticleEntityByCod(dto.getCod());
            if (existe == null) {
                throw new EntityNotFoundException(
                        "Aucun article avec le code " + dto.getCod() + " n'a été trouvé",
                        ErrorCodes.ARTICLE_NOT_FOUND
                );
            }
        }

        if(dto.getDesignation() == null || dto.getChoix() == null) {
            throw new InvalidEntityException(
                    "La désignation et le choix de l'article sont obligatoires pour la modification",
                    ErrorCodes.ARTICLE_NOT_VALID,
                    List.of("Désignation et Choix sont requises")
            );
        }

        Article existing = articleRepository.findByDesignationAndChoix(dto.getDesignation(), dto.getChoix());

        // Si on est en mode update (id != null), on ignore l'article courant
        if (existing != null && (dto.getCod() == null || !existing.getCod().equals(dto.getCod()))) {
            throw new InvalidEntityException(
                    "Un article avec cette désignation '" + dto.getDesignation() + "' et ce choix '" + dto.getChoix() + "' existe déjà.",
                    ErrorCodes.ARTICLE_DUPLICATED,
                    List.of("Désignation et choix déjà utilisés")
            );
        }
    }

    @Override
    @Transactional
    public ArticleDto save(ArticleDto dto) {
        List<String> errors = ArticleValidator.validate(dto,false);
        if (!errors.isEmpty()) {
            log.error("Article non valide {}", dto);
            throw new InvalidEntityException("L'article n'est pas valide", ErrorCodes.ARTICLE_NOT_VALID, errors);
        }


        if (dto.getRef() != null && articleRepository.existsByRef(dto.getRef())) {
            log.error("Ref article déjà utilisé {}", dto.getRef());
            throw new InvalidEntityException(
                    "La référence " + dto.getRef() + " est déjà utilisé par un autre article",
                    ErrorCodes.ARTICLE_NOT_VALID
            );
        }
        checkUnicityDesignationAndChoix(dto);
        return ArticleDto.fromEntity(articleRepository.save(ArticleDto.toEntity(dto)));
    }

    @Override
    public ArticleDto findByCod(Integer cod) {
        Article article = getActiveArticleEntityByCod(cod);
        return ArticleDto.fromEntity(article);
    }

    @Override
    public List<ArticleDto> findAll() {
        return articleRepository.findAll().stream()
                .filter(Objects::nonNull)
                .filter(article -> Boolean.TRUE.equals(article.getActif())) // garder seulement les actifs
                .map(ArticleDto::fromEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    @Override
    public PageResponse<ArticleDto> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Article> articlePage = articleRepository.findByActifTrue(pageable);

        List<ArticleDto> dtos = articlePage
                .map(ArticleDto::fromEntity)
                .getContent();

        PageResponse<ArticleDto> response = new PageResponse<>();
        response.setContent(dtos);
        response.setCurrentPage(articlePage.getNumber());
        response.setTotalPages(articlePage.getTotalPages());
        response.setTotalElements(articlePage.getTotalElements());
        response.setPageSize(articlePage.getSize());

        return response;
    }



    @Override
    @Transactional
    public ArticleDto update(Integer cod, ArticleDto dto) {
        if (dto == null) {
            log.error("DTO article est null");
            throw new InvalidEntityException("Les données de l'article sont obligatoires", ErrorCodes.ARTICLE_NOT_VALID);
        }


        Article existingArticle = getActiveArticleEntityByCod(cod);
        if (dto.getRef()!=null && !existingArticle.getRef().equals(dto.getRef())) {
            throw new InvalidEntityException(
                    "La référence de l’article ne peut pas être modifiée.",
                    ErrorCodes.ARTICLE_DUPLICATED,
                    List.of("Référence d'article non modifiable")
            );
        }

        // Créer un DTO temporaire avec les valeurs existantes pour la validation
        ArticleDto tempDto = ArticleDto.fromEntity(existingArticle);


        if (dto.getDesignation() != null) {
            tempDto.setDesignation(dto.getDesignation());
        }

        if (dto.getPrixMin() != null) {
            tempDto.setPrixMin(dto.getPrixMin());
        }
        if (dto.getStock() != null) {
            tempDto.setStock(dto.getStock());
        }
        if (dto.getPrixAchat() != null) {
            tempDto.setPrixAchat(dto.getPrixAchat());
        }
        if(dto.getTauxTva() != null) {
            tempDto.setTauxTva(dto.getTauxTva());
        }
        if (dto.getPrix() != null) {
            tempDto.setPrix(dto.getPrix());
        }

        if (dto.getFamille() != null) {
            tempDto.setFamille(dto.getFamille());
        }
        if(dto.getNote()!=null){
            tempDto.setNote(dto.getNote());
        }

        // Valider le DTO complet (existant + modifications)
        List<String> errors = ArticleValidator.validate(tempDto,true);
        if (!errors.isEmpty()) {
            log.error("Article non valide après mise à jour : {}", tempDto);
            throw new InvalidEntityException("L'article n'est pas valide", ErrorCodes.ARTICLE_NOT_VALID, errors);
        }
        checkUnicityDesignationAndChoix(dto);


        // Mettre à jour seulement les champs fournis
        if (dto.getDesignation() != null) {
            existingArticle.setDesignation(dto.getDesignation());
        }
        if (dto.getPrix() != null) {
            existingArticle.setPrix(dto.getPrix());
        }

        if (dto.getFamille() != null) {
            existingArticle.setFamille(dto.getFamille());
        }
        if(dto.getPrixMin()!=null){
            existingArticle.setPrixMin(dto.getPrixMin());
        }
        if(dto.getPrixAchat()!=null){
            existingArticle.setPrixAchat(dto.getPrixAchat());
        }
        if(dto.getStock()!=null){
            existingArticle.setStock(dto.getStock());
        }
        if(dto.getChoix()!=null){
            existingArticle.setChoix(dto.getChoix());
        }
        if(dto.getTauxTva()!=null){
            existingArticle.setTauxTva(dto.getTauxTva());
        }
        Article updatedArticle = articleRepository.save(existingArticle);
        return ArticleDto.fromEntity(updatedArticle);
    }
    @Override
    @Transactional
    public boolean delete(Integer code) {
        try {
            Article article = getActiveArticleEntityByCod(code); // vérifie et récupère l'article actif


            article.setActif(false);
            articleRepository.save(article);

            return true;

        } catch (Exception e) {
            return false; // Erreur lors de l'opération
        }
    }

    @Override
    public List<String> findDistinctFamilles() {
        return articleRepository.findDistinctFamilles();
    }
    @Override
    public List<String> findDistinctChoix() {
        return articleRepository.findDistinctChoix();
    }
    @Override
    @Transactional
    public ArticleDto updateStock(Integer cod, BigDecimal newStock) {
        if (cod == null) {
            log.error("Code article null");
            throw new InvalidEntityException("Le code de l'article est obligatoire", ErrorCodes.ARTICLE_NOT_VALID);
        }
        if (newStock == null || newStock.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Stock invalide : {}", newStock);
            throw new InvalidEntityException("Le stock doit être une valeur positive", ErrorCodes.ARTICLE_NOT_VALID);
        }

        Article article = articleRepository.findByCod(cod)
                .orElseThrow(() -> new EntityNotFoundException("Aucun article avec le code " + cod + " trouvé"));

        if (Boolean.FALSE.equals(article.getActif())) {
            throw new InvalidOperationException("L'article est inactif", ErrorCodes.ARTICLE_NOT_FOUND);
        }

        article.setStock(newStock);
        Article updatedArticle = articleRepository.save(article);
        return ArticleDto.fromEntity(updatedArticle);
    }


    @Override
    public List<ArticleDto> search(String keyword) {

        List<Article>articles= articleRepository.searchByKeyword(keyword);
        if(articles==null || articles.isEmpty()){
            throw new EntityNotFoundException("Aucun Article trouvé",ErrorCodes.ARTICLE_NOT_FOUND);
        }
        return articles.stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

}
