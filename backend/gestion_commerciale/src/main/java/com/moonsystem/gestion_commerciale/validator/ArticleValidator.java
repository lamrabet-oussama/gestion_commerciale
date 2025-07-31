package com.moonsystem.gestion_commerciale.validator;

import com.moonsystem.gestion_commerciale.dto.ArticleDto;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ArticleValidator {

    public static List<String> validate(ArticleDto dto, boolean isUpdate) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("L'article ne peut pas être null");
            return errors;
        }


        if (!isUpdate || dto.getRef() != null) {
            if (dto.getRef() == null) {
                errors.add("La référence de l'article est obligatoire");
            }
            else if (dto.getRef() <= 0) {
                errors.add("La référence de l'article doit etre > 0");
            }
        }

        if (!isUpdate || dto.getDesignation() != null) {
            if (!StringUtils.hasLength(dto.getDesignation())) {
                errors.add("La désignation de l'article est obligatoire");
            }
        }

        if (!isUpdate || dto.getPrix() != null) {
            if (dto.getPrix() == null || dto.getPrix().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Le prix d'article doit être supérieur à zéro");
            }
        }


        if (!isUpdate || dto.getPrixAchat() != null) {
            if (dto.getPrixAchat() == null || dto.getPrixAchat().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Le prix d'achat de l'article doit être supérieur à zéro");
            }
        }

        if (dto.getPrixMin() != null && dto.getPrixMin().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Le prix minimal de l'article doit être supérieur ou égal à zéro");
        }

        if (!isUpdate) {
            // En création : tauxTva est obligatoire
            if (dto.getTauxTva() == null || dto.getTauxTva().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Le taux de TVA est obligatoire et doit être supérieur à zéro.");
            } else if (dto.getTauxTva().compareTo(new BigDecimal("99.99")) > 0) {
                errors.add("Le taux de TVA ne doit pas dépasser 99.99.");
            }

        } else {
            // En mise à jour : on valide uniquement si tauxTva est renseigné
            if (dto.getTauxTva() != null) {
                if (dto.getTauxTva().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add("Le taux de TVA doit être supérieur à zéro s’il est fourni.");
                } else if (dto.getTauxTva().compareTo(new BigDecimal("99.99")) > 0) {
                    errors.add("Le taux de TVA ne doit pas dépasser 99.99.");
                }
            }
        }




        if (!isUpdate) {
            // En création : le stock est obligatoire et doit être > 0
            if (dto.getStock() == null || dto.getStock().compareTo(BigDecimal.ZERO) < 0 || dto.getStock().stripTrailingZeros().scale() > 0) {
                errors.add("Le stock est obligatoire, doit être un entier, et supérieur ou égal à zéro.");
            }
        } else {
            // En mise à jour : interdire toute tentative de modification du stock
            if (dto.getStock() != null && dto.getStock().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Le stock est obligatoire et doit être supérieur ou égale à zéro.");
            }
        }




        if (!isUpdate || dto.getFamille() != null) {
            if (dto.getFamille() == null) {
                errors.add("La famille est obligatoire");
            }
        }

        if (!isUpdate || dto.getChoix() != null) {
            if (dto.getChoix() == null) {
                errors.add("Le choix de l'article est obligatoire");
            }
        }

        return errors;
    }
}


