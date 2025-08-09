import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CaisseJourControllerService, CaisseJourDto, UserControllerService } from 'src/app/api-client';
import { from } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CaisseJourService {
  constructor(private caisseJourService: CaisseJourControllerService,private usersService:UserControllerService) {}
  getCaisseJour(userCod?: number, date?: any): Observable<CaisseJourDto> {
    return from(this.caisseJourService.getCaisseJour(userCod, date)); // Convertit le Promise en Observable
  }
  getUsers(){
    return this.usersService.getAllUsers();
  }
  downloadCaisseJour(userCod?: number, date?: any)  {
    return this.caisseJourService.downloadCaisseJourPdf(userCod, date); // Convertit le Promise en Observable

  }
}
