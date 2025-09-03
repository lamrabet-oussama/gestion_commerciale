package com.moonsystem.gestion_commerciale.services;

import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.model.Bonsorti;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BonAchatVenteService {

    BonAchatVenteDto createBon(BonAchatVenteDto dto, MvtType mvt);
    ResponseEntity<byte[]> downloadBon(Integer userCod,String serie,String mvt);
     void annulerBon(String serie);
    BonAchatVenteDto getBonAchat(Integer userId,String serie);
    BonAchatVenteDto getBonVente(Integer userId,String serie);
    List<String> getAllBonAchatSeries(Integer userId);
    List<String> getAllBonVenteSeries(Integer userId);
    BonAchatVenteDto updateBon(String serie, BonAchatVenteDto dto, MvtType mvt);
}
