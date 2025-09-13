package com.moonsystem.gestion_commerciale.controller;

import com.moonsystem.gestion_commerciale.controller.api.BonAchatVenteApi;
import com.moonsystem.gestion_commerciale.dto.BonAchatVenteDto;
import com.moonsystem.gestion_commerciale.dto.BonSortieDto;
import com.moonsystem.gestion_commerciale.dto.UserDto;
import com.moonsystem.gestion_commerciale.model.enums.MvtType;
import com.moonsystem.gestion_commerciale.repository.UserRepository;
import com.moonsystem.gestion_commerciale.services.BonAchatVenteService;
import com.moonsystem.gestion_commerciale.services.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BonAchatVenteController implements BonAchatVenteApi {

    private final BonAchatVenteService bonAchatVenteService;
    private final UserService userService;

    @Override
    public BonAchatVenteDto createBonAchat(

            BonAchatVenteDto dto
    ) {
        BonAchatVenteDto result;

            // create
            result = bonAchatVenteService.createBon(dto, MvtType.ACHAT);

        return result;


    }

    @Override
    public BonAchatVenteDto updateBonAchat(
            BonAchatVenteDto dto, String serie
    ){
        return this.bonAchatVenteService.updateBon(serie,dto, MvtType.ACHAT);
    }

    @Override
    public BonAchatVenteDto getBonAchat(Integer userId, String serie){

        //UserDto currentUser =  userService.getCurrentUser();
        return bonAchatVenteService.getBonAchat(userId,serie);
    }

    @Override
    public BonAchatVenteDto getBonVente(Integer userId,String serie){

        return bonAchatVenteService.getBonVente(userId,serie);
    }

    @Override
    public ResponseEntity<byte[]> downloadBonAchat( String serie) {
        return this.bonAchatVenteService.downloadBon( serie,MvtType.ACHAT.name());
    }
    @Override
    public BonAchatVenteDto createBonVente(

            BonAchatVenteDto dto
    ) {
        BonAchatVenteDto result;

            // create
            result = bonAchatVenteService.createBon(dto, MvtType.VENTE);

        return result;

    }
    @Override
   public BonAchatVenteDto updateBonVente(
            BonAchatVenteDto dto, String serie
    ){
        return this.bonAchatVenteService.updateBon(serie,dto, MvtType.VENTE);
    }

    @Override
    public ResponseEntity<byte[]> downloadBonVente( String serie) {
        return this.bonAchatVenteService.downloadBon( serie,MvtType.VENTE.name());
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
