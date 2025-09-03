import { Injectable } from '@angular/core';
import { TierDto, TiersService } from 'src/app/api-client';

@Injectable({
  providedIn: 'root'
})
export class TierService {

  constructor(private tierService:TiersService) {

   }

   creerTier(tier:TierDto){
    return this.tierService.createTier(tier);
   }

   supprimerTier(id:number){
    return this.tierService.deleteTier(id);
   }
   modfierTier(tier:TierDto){
    return this.tierService.updateTier(tier);
   }
   listerTiers(keyword:string,page:number,size:number){
    return this.tierService.search(keyword, page, size);
   }
   listerQualitesTiers(){
    return this.tierService.getAllTierType();
   }
   listerVillesMarocaines(){
    return this.tierService.getAllTierVilles();
   }
   findById(id:number){
    return this.tierService.findById(id);
   }
   getNumber(){
    return this.tierService.numberOfTiers();
   }
   findAllClient(){
    return this.tierService.getAllClient();
   }

   findAllFournisseur(){
    return this.tierService.getAllFournisseur();
   }
   listerAllTiers(){
    return this.tierService.getAllTiers();
   }

}
