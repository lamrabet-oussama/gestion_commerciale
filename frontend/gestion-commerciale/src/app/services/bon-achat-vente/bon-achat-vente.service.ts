import { Injectable } from '@angular/core';
import {BonsAchatVenteService, BonAchatVenteDto} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class BonAchatVenteService {

  constructor(private bonAchatVenteService: BonsAchatVenteService) { }


  createBonVente(bonVente:BonAchatVenteDto){
    return this.bonAchatVenteService.createBonVente( bonVente);
  }
  updateBonVente(bonVente:BonAchatVenteDto, serie:string){
    return this.bonAchatVenteService.updateBonVente(serie,bonVente);
  }

  annuler(serie:string){
     return this.bonAchatVenteService.deleteBon(serie);
  }

  getBonVente(userId:number|undefined, serie:string){
    return this.bonAchatVenteService.getBonVente(serie,userId);
  }
  downloadBonVente(userCode:number,serie:string){
    return this.bonAchatVenteService.downloadBonVente(1,serie);
  }

  createBonAchat(bonAchat:BonAchatVenteDto){
    return this.bonAchatVenteService.createBonAchat(bonAchat);
  }

  updateBonAchat(bonVente:BonAchatVenteDto, serie:string){
    return this.bonAchatVenteService.updateBonAchat(serie,bonVente);
  }
  getBonAchat(userId:number|undefined, serie:string|undefined){
    return this.bonAchatVenteService.getBonAchat(userId,serie);
  }
  downloadBonAchat(userCode:number,serie:string){
    return this.bonAchatVenteService.downloadBonAchat(userCode,serie);
}
getAllBonsAchatSeris(userCod:number){
    return this.bonAchatVenteService.getAllBonsAchat(userCod);
}

  getAllBonsVenteSeris(userCod:number|undefined){
    return this.bonAchatVenteService.getAllBonsVente(userCod);
  }
}
