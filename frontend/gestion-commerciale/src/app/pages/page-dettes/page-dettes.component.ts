import {Component, OnInit} from '@angular/core';
import {DettesDto, UserDto} from "../../api-client";
import {NotificationService} from "../../services/notification/notification.service";
import {DettesService} from "../../services/dettes/dettes.service";

@Component({
  selector: 'app-page-dettes',
  templateUrl: './page-dettes.component.html',
  styleUrls: ['./page-dettes.component.scss']
})
export class PageDettesComponent implements OnInit{

  dettesDto:DettesDto[]=[];
  totalChiffre:number=0;
  totalCredits:number=0;
  tauxMoyenne:number=0;
  selectedQualite:string="CLIENT";
  selectedYear:number;
  maxYear:number;

  public constructor(private dettesService: DettesService,private notificationService:NotificationService) {
    this.maxYear = new Date().getFullYear();
    this.selectedYear=new Date().getFullYear();
  }

  ngOnInit() {
    this.getDettes();
  }

  getDettes(){
    // Plus besoin de formatage - on passe directement l'année
    return this.dettesService.getDettes(this.selectedYear, this.selectedQualite).subscribe({
      next:results=>{
        this.dettesDto=results.dettes ?? [];
        this.totalCredits=results.totalCredits ?? 0;
        this.totalChiffre=results.totalChiffre ?? 0;
        this.tauxMoyenne=results.tauxMoy ?? 0;
        this.notificationService.success("Succès","Succès");
      },
      error:error=>{
        this.notificationService.error(error?.error?.message,"Erreur");
      }
    })
  }

  onYearChange(year:number): void {
    this.selectedYear=year;
    this.getDettes();
  }

  onQualiteSelected(mvt: string): void {
    this.selectedQualite = mvt;
    this.getDettes();
  }
}
