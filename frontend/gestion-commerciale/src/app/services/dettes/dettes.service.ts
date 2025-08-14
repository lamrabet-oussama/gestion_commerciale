import { Injectable } from '@angular/core';
import {DettesControllerService} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class DettesService {

  constructor(private dettesService:DettesControllerService) { }
  getDettes(start:any,mvt:string){
    return this.dettesService.getDettesWithTaux(start,mvt,undefined);
  }
}
