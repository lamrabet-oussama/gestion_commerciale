package com.moonsystem.gestion_commerciale.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.moonsystem.gestion_commerciale.controller.api.TierApi;
import com.moonsystem.gestion_commerciale.dto.TierDto;
import com.moonsystem.gestion_commerciale.services.TierService;
import com.moonsystem.gestion_commerciale.utils.PageResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Tiers", description = "API de gestion des tiers(Client,Fournisseur,Mixte)")

public class TierController implements TierApi {

    private final TierService tierService;

    public TierController(TierService tierService) {
        this.tierService = tierService;
    }

    public TierDto findById(int id) {
        return tierService.findById(id);
    }

    public TierDto createTier(TierDto tierDto) {
        return this.tierService.save(tierDto);
    }

    public TierDto updateTier(TierDto tierDto) {
        return this.tierService.update(tierDto);
    }

    public List<String> getAllTierType() {
        return tierService.getAllTierType();
    }

    public List<String> getAllTierVilles() {
        return tierService.getAllVillesMaroc();
    }

    public boolean deleteTier(int id) {
        return tierService.delete(id);
    }

    public PageResponse<TierDto> search(String keyword, int page, int pageSize) {
        return this.tierService.search(keyword, page, pageSize);
    }

    public Integer numberOfTiers() {
        return tierService.numberOfTiers();
    }

    @Override
    public List<TierDto> getAllClient() {
        return this.tierService.getAllClient();
    }

    @Override
    public List<TierDto> getAllFournisseur() {
        return this.tierService.getAllFournisseur();
    }

    @Override
    public List<TierDto> getAllTiers(){
        return this.tierService.findAllTiers();
    }
}
