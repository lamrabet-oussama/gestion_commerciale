package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.DettesDto;
import com.moonsystem.gestion_commerciale.dto.DettesResponseDto;
import com.moonsystem.gestion_commerciale.dto.TierStatistiqueCreditDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DettesService {
    DettesResponseDto getDettesCreances(int year,String qualite);
    List<BigDecimal>  getTotalDettesByTierId(Integer tierId);
    TierStatistiqueCreditDto getTierStatistiqueCreditByTierId(Integer tierId);

}
