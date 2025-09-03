import { Injectable } from '@angular/core';
import { MesInfosService, MesInfoxDto } from 'src/app/api-client';

@Injectable({
  providedIn: 'root'
})
export class EntrepriseProfileService {

  constructor(private mesInfos:MesInfosService) { }

  getInfos(){
    return this.mesInfos.findById1(1);
  }
  updateInfos(mesInfos:MesInfoxDto,file?:File |null){
    return this.mesInfos.update1(mesInfos,file ?? undefined);
  }
}
