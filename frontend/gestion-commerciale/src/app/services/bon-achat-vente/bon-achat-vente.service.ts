import { Injectable } from '@angular/core';
import {BonsAchatVenteService, BonAchatVenteDto} from "../../api-client";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class BonAchatVenteService {

  constructor(private bonAchatVenteService: BonsAchatVenteService) { }


  createBonVente(bonVente:BonAchatVenteDto){
    return this.bonAchatVenteService.createBonVente( bonVente);
  }
  updateBonVente(bonVente:BonAchatVenteDto, serie:string){
    console.log("Update bonVente");
    return this.bonAchatVenteService.updateBonVente(serie,bonVente);
  }

  annuler(serie:string){
     return this.bonAchatVenteService.deleteBon(serie);
  }

  getBonVente(serie:string,userId?:number){
    return this.bonAchatVenteService.getBonVente(serie,userId);
  }
  downloadBonVente(serie:string):Observable<Blob>{
    return this.bonAchatVenteService.downloadBonVente(serie);
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
  downloadBonAchat(serie:string):Observable<Blob>{
    return this.bonAchatVenteService.downloadBonAchat(serie);
}
getAllBonsAchatSeris(userCod?:number){
    return this.bonAchatVenteService.getAllBonsAchat(userCod);
}

  getAllBonsVenteSeris(userCod?:number){
    return this.bonAchatVenteService.getAllBonsVente(userCod);
  }
}
