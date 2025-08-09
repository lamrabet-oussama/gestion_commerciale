package com.moonsystem.gestion_commerciale.services.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.moonsystem.gestion_commerciale.dto.TierDto;
import com.moonsystem.gestion_commerciale.exception.EntityNotFoundException;
import com.moonsystem.gestion_commerciale.exception.ErrorCodes;
import com.moonsystem.gestion_commerciale.exception.InvalidEntityException;
import com.moonsystem.gestion_commerciale.model.Tier;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.model.enums.VilleMaroc;
import com.moonsystem.gestion_commerciale.repository.TierRepository;
import com.moonsystem.gestion_commerciale.services.TierService;
import com.moonsystem.gestion_commerciale.utils.BeanCopyUtils;
import com.moonsystem.gestion_commerciale.utils.PageResponse;

@Service
public class TierServiceImpl implements TierService {

    private final TierRepository tierRepository;

    public TierServiceImpl(TierRepository tierRepository) {
        this.tierRepository = tierRepository;
    }

    public TierDto findById(int id) {
        return TierDto.fromEntity(tierRepository.findById(id).orElse(new Tier()));
    }

    @Override
    public TierDto save(TierDto dto) {
        Optional<Tier> existingTier = tierRepository.findByRef(dto.getRef());

        if (existingTier.isPresent()) {
            throw new InvalidEntityException(
                    "Tier avec cette référence existe déjà : " + dto.getRef(),
                    ErrorCodes.TIER_ALREADY_IN_USE,
                    List.of("Référence déjà utilisée")
            );
        }

        Tier entity = TierDto.toEntity(dto);
        Tier saved = tierRepository.save(entity);
        return TierDto.fromEntity(saved);
    }

    @Override
    public PageResponse<TierDto> search(String keyword, int page, int size) {
        // Sécurité sur les valeurs
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "nom"));

        // Récupération de la page d'entités
        Page<Tier> tierPage = tierRepository.searchByKeyword(keyword, pageable);

        // Si aucun résultat, renvoyer une page vide avec les infos correctes
        if (tierPage.isEmpty()) {
            return new PageResponse<>(
                    Collections.emptyList(),
                    page, // numéro de page demandé
                    0, // total pages
                    0, // total éléments
                    size // taille par page
            );
        }

        // Transformation en DTOs
        Page<TierDto> dtoPage = tierPage.map(TierDto::fromEntity);

        return new PageResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getTotalPages(),
                dtoPage.getTotalElements(),
                dtoPage.getSize()
        );
    }

    @Override
    public TierDto update(TierDto dto) {
        Tier existingTier = tierRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tier non trouvé", ErrorCodes.TIER_NOT_FOUND));

        if (dto.getRef() != null && !dto.getRef().equals(existingTier.getRef()) && tierRepository.existsByRef(dto.getRef())) {
            throw new InvalidEntityException("Tier avec ce référence déjà existe: " + dto.getRef(), ErrorCodes.TIER_ALREADY_IN_USE, List.of("Référence déjà existe"));
        }
        // Copier uniquement les champs non-nuls du DTO vers l'entité
        BeanCopyUtils.copyNonNullProperties(dto, existingTier);

        // dto -> filtrer null -> fill
        // Sauvegarder et retourner le DTO
        Tier updated = tierRepository.save(existingTier);
        return TierDto.fromEntity(updated);
    }

    @Override
    public TierDto findById(Integer id) {
        return tierRepository.findById(id)
                .map(TierDto::fromEntity).orElseThrow(() -> new EntityNotFoundException(
                "Tier not found with id: " + id, ErrorCodes.TIER_NOT_FOUND));
    }

    @Override
    public boolean delete(Integer id) {
        try {
            Optional<Tier> tier = tierRepository.findById(id);
            if (tier.isPresent()) {
                Tier t = tier.get();
                t.setActif(false);
                tierRepository.save(t);
            }
            return true;

        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Tier not found with id: " + id, ErrorCodes.TIER_NOT_FOUND);
        }

    }

    @Override
    public List<String> getAllTierType() {
        return Arrays.stream(TypeTier.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public List<String> getAllVillesMaroc() {
        return Arrays.stream(VilleMaroc.values())
                .map(Enum::name)
                .toList();
    }

    @Override
    public Integer numberOfTiers() {
        return (int) tierRepository.count();
    }
}
