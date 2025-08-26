package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.BonAchatVenteApi;
import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.dto.BonSortieDto;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.services.BonAchatVenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BonAchatVenteController implements BonAchatVenteApi {

    private final BonAchatVenteService bonAchatVenteService;

    @Override
    public BonAchatVenteDto createBonAchat(

            BonAchatVenteDto dto
    ) {
        return bonAchatVenteService.createBon(dto, MvtType.ACHAT);


    }


    @Override
    public BonAchatVenteDto getBonAchat(Integer userId, String serie){

        return bonAchatVenteService.getBonAchat(userId,serie);
    }

    @Override
    public BonAchatVenteDto getBonVente(Integer userId,String serie){

        return bonAchatVenteService.getBonVente(userId,serie);
    }

    @Override
    public ResponseEntity<byte[]> downloadBonAchat(Integer userCod, String serie) {
        return this.bonAchatVenteService.downloadBon(userCod, serie,MvtType.ACHAT.name());
    }
    @Override
    public BonAchatVenteDto createBonVente(

            BonAchatVenteDto dto
    ) {
        return bonAchatVenteService.createBon(dto, MvtType.VENTE);


    }


    @Override
    public ResponseEntity<byte[]> downloadBonVente(Integer userCod, String serie) {
        return this.bonAchatVenteService.downloadBon(userCod, serie,MvtType.VENTE.name());
    }

    @Override
    public void deleteBon(String serie){
         this.bonAchatVenteService.annulerBon(serie);
    }


    @Override
    public List<String> getAllBonsAchat(Integer userCod){
        return this.bonAchatVenteService.getAllBonAchatSeries(userCod);
    }
    @Override
    public List<String> getAllBonsVente(Integer userCod){
        return this.bonAchatVenteService.getAllBonVenteSeries(userCod);
    }
}
