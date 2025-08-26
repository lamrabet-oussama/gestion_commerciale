package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;

import java.util.List;

public interface ReglementService {

    ReglementDto ajouterReglement(ReglementDto reglementDto);
    ReglementResponseDto listerReglements(Integer userId, Integer tierId);
    ReglementDto updateReglement(ReglementDto reglementDto);
    void deleteReglement(Integer reglementId);
}
