package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.DettesApi;
import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.dto.TierStatistiqueCreditDto;
import com.moonsystem.gestion_commerciale.model.enums.TypeTier;
import com.moonsystem.gestion_commerciale.services.DettesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DettesController implements DettesApi {

    private final DettesService dettesService;

    @Override
    public DettesResponseDto getDettes(int year, String qualite) {
    return this.dettesService.getDettesCreances(year,qualite);
    }

    @Override
    public BigDecimal  getCreditByTierId(@PathVariable("id") Integer tierId){
        return this.dettesService.getTotalDettesByTierId(tierId);
    }

    @Override
    public TierStatistiqueCreditDto getStsByTierId( Integer tierId,Integer year){
        return this.dettesService.getTierStatistiqueCreditByTierId(tierId, year);
    }


}
