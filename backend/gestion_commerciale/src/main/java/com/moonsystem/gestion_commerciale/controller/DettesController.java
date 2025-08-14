package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.DettesApi;
import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.services.DettesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DettesController implements DettesApi {

    private final DettesService dettesService;

    @Override
    public DettesResponseDto getDettesWithTaux(int year, String mvt) {
        return dettesService.getDettesWithStats(year, mvt);
    }
}
