package com.moonsystem.gestion_commerciale.services.impl;

import java.util.Arrays;
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
        return TierDto.fromEntity(tierRepository.save(TierDto.toEntity(dto)));
    }

    @Override
    public PageResponse<TierDto> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "nom"));

        // Récupération de la page d'entités
        Page<Tier> tierPage = tierRepository.searchByKeyword(keyword, pageable);
        System.out.println("Résultats trouvés : " + tierPage.getContent().size());
        tierPage.getContent().forEach(System.out::println);
        // Transformation en DTOs
        Page<TierDto> dtoPage = tierPage.map(TierDto::fromEntity);
        System.out.println(dtoPage);
        // Retour d'une PageResponse personnalisée
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
}
