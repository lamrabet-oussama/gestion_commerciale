import { Injectable } from '@angular/core';
import {Reglement} from "../../api-client/model/reglement";
import {Observable} from "rxjs";
import {ReglementDto} from "../../api-client";
import {RglementsService} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class ReglementService {

  constructor(private reglementService:RglementsService) { }

  createReglement(reglementDto:ReglementDto) {
    return this.reglementService.createReglement(reglementDto);
  }
  listerReglement(tierId:number,userId?:number,date?:string,year?:number){
    return this.reglementService.listerReglements(tierId,userId,date,year);
  }
  getReglement(id:number){
    return this.reglementService.getReglement(id);
  }

  updateReglement(reglementDto:ReglementDto) {
    return this.reglementService.updateReglement(reglementDto);
  }
  deleteReglement(regId:number){
    return this.reglementService.deleteReglement(regId);
  }


  downloadReglement(tierId:number,userCod?:number,date?:string,year?:number){
    return this.reglementService.downloadReglementPdf(tierId,userCod,date,year);
  }


}
