package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface ReglementService {

    ReglementDto ajouterReglement(ReglementDto reglementDto);
    ReglementResponseDto listerReglements(Integer userId, Integer tierId, LocalDateTime startDate,Integer year);
    ReglementDto updateReglement(ReglementDto reglementDto);
    void deleteReglement(Integer reglementId);
    ResponseEntity<byte[]> downloadRegPdf(Integer userCod, Integer tierId,LocalDateTime date,Integer year);
    ReglementDto getReglementById(Integer reglementId);

}
