import {Component, OnInit} from '@angular/core';
import {TierDto, TierStatistiqueCreditDto} from "../../api-client";
import {TierService} from "../../services/tier/tier.service";
import {DettesService} from "../../services/dettes/dettes.service";

import {NotificationService} from "../../services/notification/notification.service";

@Component({
  selector: 'app-tier-situation',
  templateUrl: './tier-situation.component.html',
  styleUrls: ['./tier-situation.component.scss']
})
export class TierSituationComponent implements OnInit{
   tiers:TierDto[]=[];
   selectedTierId:number=-1;
  selectedYear:number;
  maxYear:number;
  tierStatistiques:TierStatistiqueCreditDto= {
    bonsorties:[],
    reglements: [],
    totalCredit: 0,
    totalDebit: 0,
    resteAPayer:0,
    percentageDebitCredit:0
  }  as TierStatistiqueCreditDto;
   soldeTier: number=0;

  public constructor(private dettesService: DettesService,private tierService: TierService,private notificationService: NotificationService) {
    this.maxYear = new Date().getFullYear();
    this.selectedYear=new Date().getFullYear();
  }

  ngOnInit() {
    this.listerTiers();
  }

  listerTiers(){
    this.tierService.listerAllTiers().subscribe(
      {
        next:(tiers)=>{
          this.tiers = tiers;
        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      }
    )
  }
  getTierSituation(){
    this.dettesService.getTierSituation(this.selectedTierId, this.selectedYear).subscribe(
      {
        next:(situation)=>{
          this.tierStatistiques = situation;
        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      }
    )
  }
  getTierSolde(selected: number) {
    this.dettesService.getSoldeByTierId(selected).subscribe({
      next:(data)=>{
        this.soldeTier = data;

      },
      error:(error)=>{
        this.notificationService.error(error?.error?.message,'Erreur');
      }
    })
  }
  onSelectTierIdChange(selected: number) {
    this.getTierSolde(selected);
    this.getTierSituation();
  }


}
