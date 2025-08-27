package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.ReglementApi;
import com.moonsystem.gestion_commerciale.dto.ReglementDto;
import com.moonsystem.gestion_commerciale.dto.ReglementResponseDto;
import com.moonsystem.gestion_commerciale.services.ReglementService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReglementController implements ReglementApi {
    private final ReglementService reglementService;
    @Override
public ReglementDto createReglement(@RequestBody ReglementDto dto){
        return
                this.reglementService.ajouterReglement(dto);
    }

    @Override
    public ReglementResponseDto listerReglements( Integer userId, Integer tierId){
        return this.reglementService.listerReglements(userId,tierId);
    }

    @Override
    public   ReglementDto updateReglement(ReglementDto dto){
        return this.reglementService.updateReglement(dto);
    }

    @Override
    public void deleteReglement(Integer  id){
         this.reglementService.deleteReglement(id);
    }

    @Override
    public ResponseEntity<byte[]> downloadReglementPdf (
             Integer userCod,
            Integer tierId
    ){
        return this.reglementService.downloadRegPdf(userCod,tierId);
    }
}
