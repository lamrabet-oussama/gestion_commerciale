import { Injectable } from '@angular/core';
import {DettesControllerService} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class DettesService {

  constructor(private dettesService:DettesControllerService) { }
  getDettes(year:number,qualite:string){
    return this.dettesService.getDettes(year,qualite);
  }

  getSoldeByTierId(tierId:number) {
    return this.dettesService.getCreditByTierId(tierId);
  }
  getTierSituation(tierId:number,year:number){
    return this.dettesService.getStsByTierId(tierId,year);
  }

}
