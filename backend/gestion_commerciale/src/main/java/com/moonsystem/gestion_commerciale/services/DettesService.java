package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface DettesService {
    DettesResponseDto getDettesWithStats(int year, String mvt);
}
