import {Component, OnInit} from '@angular/core';
import {TierService} from "../../services/tier/tier.service";
import {DettesService} from "../../services/dettes/dettes.service";
import {NotificationService} from "../../services/notification/notification.service";
import {ReglementService} from "../../services/reglement/reglement.service";
import {ReglementDto, ReglementResponseDto, TierDto, UserDto} from "../../api-client";
import {faTimes} from "@fortawesome/free-solid-svg-icons/faTimes";

@Component({
  selector: 'app-reglement',
  templateUrl: './reglement.component.html',
  styleUrls: ['./reglement.component.scss']
})
export class ReglementComponent implements OnInit{
  selectedDate: string = '';
  maxDate: string = '';
  searchMode: 'date' | 'year' = 'date';
  username:string='';
  espece:number=0;
  cheque:number=0;
  selectedTierId:number=-1;
  selectedYear:number;
  maxYear:number;
  selectedUserCod:number=1;
  regNumber:number=-1;
  soldeTier:number=0;
  faX = faTimes;

  tiers:TierDto[]=[];
  deteailsCheque:string='';
  reglementDto:ReglementDto={} as ReglementDto;
  reglementResponse: ReglementResponseDto = {
    reglements: [],
    totalEspece: 0,
    totalCheque: 0
  } as ReglementResponseDto;
  public constructor(
    private reglementService: ReglementService,
    private tierService: TierService,
    private dettesService: DettesService,
    private notificationService: NotificationService
  ){
    this.maxYear = new Date().getFullYear();
    this.selectedYear=new Date().getFullYear();
  }

  async ngOnInit() {

    // Ne définir la date d'aujourd'hui que si aucune date n'est chargée
    if (!this.selectedDate) {
       this.setTodayAsDefault();
    }
    this.listerTiers();
  }
  private formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0];
  }
  private  setTodayAsDefault() {
    const today = new Date();
    this.selectedDate = this.formatDateForInput(today);
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

  listerReglements() {
    let dateParam: string|undefined  = undefined;
    let yearParam: number|undefined  = undefined;

    if (this.searchMode === 'date') {
      dateParam = `${this.selectedDate}T00:00:00`;
    } else if (this.searchMode === 'year') {
      yearParam = this.selectedYear;
    }

    console.log("UserId:",this.selectedUserCod);
    this.reglementService.listerReglement(
      this.selectedTierId,
      this.selectedUserCod,
      dateParam,
      yearParam
    ).subscribe({
      next: (regs) => {
        this.reglementResponse = regs;
        console.log("Regs:",regs);
        if (regs.reglements?.length == 1) {
          this.regNumber = regs.reglements[0].id ?? -1;
          this.username = regs.reglements[0].nomUser ?? '';
        }
      },
      error: (error) => {
        this.notificationService.error(error?.error?.message, 'Erreur');
      }
    });
  }

  ajouterReglement(){
    this.reglementDto.cheque=Number(this.cheque);
    this.reglementDto.espece=Number(this.espece);
    this.reglementDto.detailsCheque=this.deteailsCheque;
    this.reglementDto.idUser=this.selectedUserCod;
    this.reglementDto.idTier=this.selectedTierId;
    console.log("Reg avant:",this.reglementDto);
    this.reglementService.createReglement(this.reglementDto).subscribe(
      {
        next:(data)=>{
          this.listerReglements();
          this.getTierSolde(this.selectedTierId);
          this.notificationService.success('Reglement added successfully.',"Succès");

        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message, 'Erreur');
        }
      }
    )
  }

  modifierReglement(){
    if(this.reglementDto==null){
      this.notificationService.error('Sélectionnez un Réglement.',"Erreur");

      return;
    }
    this.reglementDto.cheque=Number(this.cheque);
    this.reglementDto.espece=Number(this.espece);
    this.reglementDto.detailsCheque=this.deteailsCheque;
    this.reglementDto.idUser=this.selectedUserCod;
    this.reglementDto.idTier=this.selectedTierId;
    console.log("Reg avant:",this.reglementDto);
    this.reglementService.updateReglement(this.reglementDto).subscribe(
      {
        next:(data)=>{
          this.listerReglements();
          this.getTierSolde(this.selectedTierId);
          this.notificationService.success('Reglement modifié avec succès.',"Succès");

        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message, 'Erreur');
        }
      }
    )
  }

  getReglement(idReg:number){
    this.reglementService.getReglement(idReg).subscribe(
      {
        next:(result)=>{
          this.reglementDto=result;
          this.espece=result.espece ?? 0;
          this.cheque=result.cheque ?? 0;
          this.deteailsCheque=result.detailsCheque ?? '';
          console.log("Reg:",this.reglementDto);
          this.notificationService.success("Reglement Récupéré avec succès","Succès");
        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message, 'Erreur');
        }
      }
    )
  }


  annulerReglement(idReg:number){
    this.reglementService.deleteReglement(idReg).subscribe(
      {
        next:()=>{
          this.reglementResponse={} as ReglementResponseDto;
          this.notificationService.success("Reglement Supprimé avec succès","Succès");
        },
        error:(error)=>{
          this.notificationService.error(error?.error?.message, 'Erreur');
        }
      }
    )
  }

  downloadReglementPdf() {

    if(this.selectedTierId==-1){
      this.notificationService.error("Sélectionner un tier",'Erreur');
      return;
    }
    let dateParam: string|undefined  = undefined;
    let yearParam: number|undefined  = undefined;

    if (this.searchMode === 'date') {
      dateParam = `${this.selectedDate}T00:00:00`;
    } else if (this.searchMode === 'year') {
      yearParam = this.selectedYear;
    }
    this.reglementService
      .downloadReglement(
        this.selectedTierId,
        this.selectedUserCod,
        dateParam,
        yearParam
      )
      .subscribe({
        next: (pdf: Blob) => {
          const fileName = `Reglement${this.selectedDate}.pdf`; // nom voulu
          const fileURL = URL.createObjectURL(pdf);
          const a = document.createElement('a');
          a.href = fileURL;
          a.download = fileName;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          URL.revokeObjectURL(fileURL);
        },
        error: (err) => {
          console.log(err);
          this.notificationService.error(err.error.message, 'Erreur');
        },
      });
  }
  resetValues(){
    this.espece=0;
    this.cheque=0;
    this.deteailsCheque='';
  }

  getTierSolde(selected: number) {
   this.dettesService.getSoldeByTierId(selected).subscribe({
     next:(data)=>{
       this.soldeTier = data;
       this.espece=0;
       this.cheque=0;
       this.deteailsCheque='';
       this.listerReglements();
     },
     error:(error)=>{
       this.notificationService.error(error?.error?.message,'Erreur');
     }
   })
  }
  onSelectTierIdChange(selected: number) {
    this.getTierSolde(selected);
    this.listerReglements();
  }
  onYearChange() {
    this.listerReglements();
  }
  onDateChange(){
    this.listerReglements();
  }


}
