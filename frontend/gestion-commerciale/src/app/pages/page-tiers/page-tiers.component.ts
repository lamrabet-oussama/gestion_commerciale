import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faTimes } from '@fortawesome/free-solid-svg-icons/faTimes';
import { TierDto } from 'src/app/api-client';
import { NotificationService } from 'src/app/services/notification/notification.service';
import { TierService } from 'src/app/services/tier/tier.service';

@Component({
  selector: 'app-page-tiers',
  templateUrl: './page-tiers.component.html',
  styleUrls: ['./page-tiers.component.scss'],
})
export class PageTiersComponent implements OnInit {
  tierDto: TierDto = this.getEmptyTier();
  tiers: TierDto[] = [];
  qualites: string[] = [];
  villes: string[] = [];
  faX = faTimes;
errorMsgValidation: string[] = [];
  keyword = '';
  page = 0;
  size = 10;
  totalPages = 0;

  constructor(
    private tierService: TierService,
    private router: Router,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  /** ---------- Initialisation ---------- **/
  private loadInitialData() {
    this.listerTiers();
    this.fetchList('qualites');
    this.fetchList('villes');
    this.getNumber();
  }

  /**-----------Data Validation------- **/
  validerArticle(tierDto:TierDto): boolean {
    this.errorMsgValidation = []; // Reset des erreurs avant validation
    let valide = true;

    if (!tierDto.ref || tierDto.ref <= 0) {
      this.errorMsgValidation.push("La référence de personne est obligatoire.");
      valide = false;
    }
    if (!tierDto.nom || tierDto.nom.trim() === '') {
      this.errorMsgValidation.push("Le nom de personne est obligatoire.");
      valide = false;
    }

    if (!tierDto.qualite || tierDto.qualite.trim() === '') {
      this.errorMsgValidation.push('La qualité de personne est obligatoire.');
      valide = false;
    }


    if (
      !this.tierDto.ville ||
      this.tierDto.ville.trim() === ''
    ) {
      this.errorMsgValidation.push('La ville est obligatoire.');
      valide = false;
    }
    if (!this.tierDto.gsm || this.tierDto.gsm.trim() === '') {
      this.errorMsgValidation.push('Le GSM est obligatoire.');
      valide = false;
    }

    this.notification.error("Veuillez remplir les champs obligatoires",'Erreur')

    return valide;
  }

  /** ---------- Actions CRUD ---------- **/
  creerTier() {
    if(this.validerArticle(this.tierDto)){


    this.tierService.creerTier(this.tierDto).subscribe({
      next: () => {
        this.handleSuccess('Tier créé avec succès');
        this.listerTiers();
        this.cancel();
      },
      error: (err) =>{
        this.notification.error(err?.error?.message,'Erreur')
        this.handleError(err, "Erreur lors de l'enregistrement du tier")}
    });
  }}

  modifierTier() {
    this.tierService.modfierTier(this.tierDto).subscribe({
      next: () => {
        this.handleSuccess('Tier modifié avec succès');
        this.listerTiers();
      },
      error: (err) =>
        this.handleError(err, 'Erreur lors de la modification du tier'),
    });
  }

  deleteTier(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette personne ?')) {
      this.tierService.supprimerTier(id).subscribe({
        next: () => {
          this.handleSuccess('Tier supprimé avec succès');
          this.listerTiers();
        },
        error: (err) => this.handleError(err, 'Erreur lors de la suppression'),
      });
    }
  }

  findById(id: number) {
    this.tierService.findById(id).subscribe({
      next: (tier) => (this.tierDto = tier),
      error: (err) =>
        this.handleError(err, 'Erreur lors de la récupération du tier'),
    });
  }

  /** ---------- Pagination ---------- **/
  listerTiers() {
    this.tierService.listerTiers(this.keyword, this.page, this.size).subscribe({
      next: (res) => {
        if (res?.content) {
          this.tiers = res.content;
          this.totalPages = res.totalPages ?? 0;
          this.page = res.currentPage ?? 0;
        }
      },
      error: (err) =>{
        console.log(err);
        this.handleError(err, 'Erreur lors du chargement des tiers')
    }});
  }

  onPageChange(page: number) {
    this.page = page;
    this.listerTiers();
  }
  onSearchChange() {
    this.page = 0; // recommencer à la première page
    this.listerTiers();
  }
  /** ---------- Helpers ---------- **/
  private fetchList(type: 'qualites' | 'villes') {
    const serviceCall =
      type === 'qualites'
        ? this.tierService.listerQualitesTiers()
        : this.tierService.listerVillesMarocaines();

    serviceCall.subscribe({
      next: (res) => {
        if (type === 'qualites') this.qualites = res;
        else this.villes = res;
      },
      error: (err) =>
        this.handleError(err, 'Erreur lors du chargement des ' + type),
    });
  }

  getNumber() {
    this.tierService.getNumber().subscribe({
      next: (num) => (this.tierDto.ref = num + 1),
      error: (err) =>
        this.handleError(err, 'Erreur lors de la récupération du numéro'),
    });
  }

  cancel() {
    this.tierDto = this.getEmptyTier();
  }

  private getEmptyTier(): TierDto {
    return {
      qualite: '',
      type: '',
      ref: 0,
      nom: '',
      adresse: '',
      ville: '',
      fon: '',
    };
  }

  private handleError(err: any, message: string) {
    this.notification.error(err.error?.message, 'Erreur');
  }

  private handleSuccess(message: string) {
    this.notification.success(message, 'Succès');
  }
}
