package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.FluxNormalApi;
import com.moonsystem.gestion_commerciale.dto.FluxNormalDto;
import com.moonsystem.gestion_commerciale.dto.FluxNormalResponseDto;
import com.moonsystem.gestion_commerciale.services.FluxNormalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FluxNormalController implements FluxNormalApi {

    private final FluxNormalService fluxNormalService;
@Override
    public FluxNormalResponseDto getFluxArticle(Integer articleId, Integer year){
    return this.fluxNormalService.getFluxNormalResponseArticle(articleId,year);
}
}
