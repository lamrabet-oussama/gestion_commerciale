package com.moonsystem.gestion_commerciale.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.moonsystem.gestion_commerciale.controller.api.CaisseJourApi;
import com.moonsystem.gestion_commerciale.dto.CaisseJourDto;
import com.moonsystem.gestion_commerciale.services.CaisseJourService;

@RestController
public class CaisseJourController implements CaisseJourApi {

    private final CaisseJourService caisseJourService;

    public CaisseJourController(CaisseJourService caisseJourService) {
        this.caisseJourService = caisseJourService;

    }

    @Override
    public CaisseJourDto getCaisseJour(Integer userCod, LocalDateTime startDate) {
        return this.caisseJourService.getCaisseJourDtos(userCod, startDate);
    }

    @Override
    public ResponseEntity<byte[]> downloadCaisseJourPdf(Integer userCod, LocalDateTime startDate) {
        return this.caisseJourService.downloadCaissePdf(userCod, startDate);
    }

}
