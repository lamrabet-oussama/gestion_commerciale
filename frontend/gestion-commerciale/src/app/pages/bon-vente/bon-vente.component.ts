import {Component, OnInit} from '@angular/core';
import {ArticleService} from "../../services/article/article.service";
import {TierService} from "../../services/tier/tier.service";
import {BonAchatVenteService} from "../../services/bon-achat-vente/bon-achat-vente.service";
import {ArticleAddBonDto, ArticleDto, BonAchatVenteDto, TierDto, UserDto} from "../../api-client";
import {NotificationService} from "../../services/notification/notification.service";
import { IndexedDBService } from '../../services/indexedDB/indexed-db.service';
import { firstValueFrom } from 'rxjs';

import {Observable} from "rxjs";
import {Store} from "@ngrx/store";
import {AppState} from "../../app.state";
import {selectClientsList} from "../../../store/clients/clients.selectors";
import {loadClients} from "../../../store/clients/clients.actions";
import {faTimes} from "@fortawesome/free-solid-svg-icons/faTimes";
import {DettesService} from "../../services/dettes/dettes.service";

@Component({
  selector: 'app-bon-vente',
  templateUrl: './bon-vente.component.html',
  styleUrls: ['./bon-vente.component.scss']
})
export class BonVenteComponent implements OnInit {

  selectedSerie: string = "";
  allSeris: string[] = [];
  userId: number | undefined = 1;
  selectedClientId: number = -1;
  clients$: Observable<TierDto[]>;
  articlesAddToBon: ArticleAddBonDto[] = [];
  selectedDate: string = '';
  selectedDesignation: string = '';
  selectedChoix: string = '';
  espece: number = 0;
  cheque: number = 0;
  credit: number = 0;
  soldeClient: number = 0;
  totalVenteAvecRemise: number = 0;
  totalVenteBrute: number = 0;
  remiseTotal: number = 0;
  detailsCheque: string = "";
  isSerieDisabled: boolean = true;
  faX = faTimes;
  selectedUser: UserDto = {} as UserDto;
  username:string = "";
  maxDate: string = '';
  designations: string[] = [];
  choix: string[] = [];
  articleAddBonDto: ArticleAddBonDto = {};
  bonVenteDto: BonAchatVenteDto = {} as BonAchatVenteDto;
  remiseGlobale: number=0;
  remiseUnitairesTotal: number = 0;

  // Clé pour localStorage
  //private readonly STORAGE_KEY = 'bonVente_data';


  public constructor(
    private articleService: ArticleService,
    private dettesService: DettesService,
    private store: Store<AppState>,
    private bonVenteService: BonAchatVenteService,
    private notificationService: NotificationService,
    private indexedDbService: IndexedDBService
  ) {
    this.clients$ = this.store.select(selectClientsList)
    this.clients$.subscribe(clients => {
      console.log("All Clients:", clients)
    });
  }

  async ngOnInit() {
    await this.loadAllDataFromDb(); // Charger toutes les données sauvegardées
    await this.getAllBonVSeris();
    this.store.dispatch(loadClients());
    await this.getAllDesignations();
    this.initializeDateLimits();

    // Ne définir la date d'aujourd'hui que si aucune date n'est chargée
    if (!this.selectedDate) {
      await this.setTodayAsDefault();
    }
  }

  protected async resetValues() {
    this.espece = 0;
    this.totalVenteAvecRemise = 0;
    this.totalVenteBrute = 0;
    this.cheque = 0;
    this.detailsCheque = "";
    this.soldeClient=0;
    this.credit = 0;
    this.bonVenteDto = {} as BonAchatVenteDto;
    this.articlesAddToBon = [];
    this.selectedClientId = -1;
    this.selectedSerie="";
    this.selectedDesignation = '';
    this.selectedChoix = '';
    this.remiseTotal = 0;
    this.articleAddBonDto = {};
    await this.setTodayAsDefault();

    // Sauvegarder après reset
    await this.saveAllDataToDb();
  }

  async toggleSerie() {
    this.isSerieDisabled = !this.isSerieDisabled;
    if (this.isSerieDisabled) {
      await this.resetValues();
      this.selectedSerie = '';
      this.username="";
    }
    await this.saveAllDataToDb();
  }

  protected async getTierSolde(selectedClientId: number) {
    try {
      const result = await firstValueFrom(this.dettesService.getSoldeByTierId(selectedClientId));
      this.soldeClient = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  private async getAllBonVSeris() {
    try {
      const data = await firstValueFrom(this.bonVenteService.getAllBonsVenteSeris(this.userId));
      this.allSeris = data;
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  private initializeDateLimits(): void {
    const today = new Date();
    this.maxDate = this.formatDateForInput(today);
  }

  private async getChoixByDesignation(designation: string) {
    try {
      const result = await firstValueFrom(this.articleService.getChoixByDes(designation));
      this.choix = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  async onDesignationChange(designation: string) {
    this.selectedDesignation = designation;
    this.selectedChoix = '';
    this.choix = [];

    if (!designation) {
      await this.saveAllDataToDb();
      return;
    }

    await this.getChoixByDesignation(designation);
  }

  async onChoixChange(choix: string) {
    this.selectedChoix = choix;

    if (!this.selectedDesignation) {
      this.choix = [];
      this.notificationService.error("Vous devez sélectionner une designation", 'Erreur');
      await this.saveAllDataToDb();
      return;
    }
    if (!this.selectedChoix) {
      this.choix = [];
      this.notificationService.error("Vous devez sélectionner un choix", 'Erreur');
      await this.saveAllDataToDb();
      return;
    }

    await this.findArticle(this.selectedDesignation, this.selectedChoix);
  }

  private async findArticle(designation: string, choix: string) {
    try {
      const result = await firstValueFrom(this.articleService.findArticleByDesignationAndChoix(designation, choix));
      this.articleAddBonDto = result;
      console.log("Article:", result);
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  private async getAllDesignations() {
    try {
      const result = await firstValueFrom(this.articleService.getAllDesognations());
      this.designations = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  protected async getBonVente() {
    if (!this.selectedSerie) {
      this.bonVenteDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      await this.saveAllDataToDb();
      return;
    }

    try {
      const result = await firstValueFrom(this.bonVenteService.getBonVente(this.userId, this.selectedSerie));
      this.bonVenteDto = result;
      this.espece = result.espece ?? 0;
      this.cheque = result.cheque ?? 0;
      this.selectedClientId = result.idTier ?? -1;
      this.userId = result.idUser ?? -1;
      this.username=result.nomUser ?? "";
      this.detailsCheque = result.detCheque ?? "";
      this.articlesAddToBon = result.articles ?? [];
      this.totalVenteBrute = result.montantSansRemise ?? 0;
      this.totalVenteAvecRemise = result.montant ?? 0;
      this.remiseGlobale = result.remis ?? 0;
      this.selectedDate = result.datBon
        ? new Date(result.datBon).toISOString().split('T')[0]
        : new Date().toISOString().split('T')[0];
      console.log("Bon Vente:", this.bonVenteDto);
      await this.getTierSolde(this.selectedClientId);
      await this.recalculateTotal();
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, "Erreur");
    }
  }

  private formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private async setTodayAsDefault(): Promise<void> {
    const today = new Date();
    this.selectedDate = this.formatDateForInput(today);
    await this.saveAllDataToDb();
  }




  protected async recalculateTotal(): Promise<void> {
    const totalBrutInitial = this.articlesAddToBon.reduce((sum, a) => {
      const prix = Number(a.prix ?? 0);
      const qte  = Number(a.quantite ?? 0);
      return sum + (prix * qte);
    }, 0);

    const totalRemiseUnit = this.articlesAddToBon.reduce((sum, a) => {
      const rUnit = Number(a.remisUni ?? 0);
      const prix  = Number(a.prix ?? 0);
      const qte   = Number(a.quantite ?? 0);
      const rUnitCapped = Math.max(0, Math.min(rUnit, prix));
      return sum + (rUnitCapped * qte);
    }, 0);

    // STOCKER la somme des remises unitaires
    this.remiseUnitairesTotal = this.round2(totalRemiseUnit);

    // SIMPLE : remiseGlobale = remiseTotal + remiseUnitairesTotal
    this.remiseGlobale = this.round2(this.toNumber(this.remiseTotal) + this.remiseUnitairesTotal);

    this.totalVenteBrute = this.round2(totalBrutInitial);
    this.totalVenteAvecRemise = this.round2(this.totalVenteBrute - this.remiseGlobale);

    if (this.totalVenteAvecRemise < 0) this.totalVenteAvecRemise = 0;

    this.credit = this.round2(
      (this.toNumber(this.espece) + this.toNumber(this.cheque)) - this.totalVenteAvecRemise
    );

    console.log({
      totalBrutInitial: this.totalVenteBrute,
      remiseUnitaires: this.remiseUnitairesTotal,
      remiseTotale: this.remiseTotal, // Saisie utilisateur
      remiseGlobale: this.remiseGlobale, // Calculée
      totalVenteAvecRemise: this.totalVenteAvecRemise,
      credit: this.credit,
    });

    await this.saveAllDataToDb();
  }

  private round2(n: number): number {
    return Math.round((n + Number.EPSILON) * 100) / 100;
  }

  protected async ajouterListe(): Promise<void> {
    if (!this.articleAddBonDto || !this.articleAddBonDto.cod) {
      this.notificationService.error("Aucun article sélectionné", "Erreur");
      return;
    }

    const qtyToAdd = ((this.articleAddBonDto.quantite == undefined || this.articleAddBonDto.quantite <= 0) ? 1 : this.articleAddBonDto.quantite);
    const existing = this.articlesAddToBon.find(a => a.cod === this.articleAddBonDto.cod);

    if (existing) {
      const newQty = (existing.quantite ?? 0) + qtyToAdd;
      if (newQty > (this.articleAddBonDto.stock ?? Infinity)) {
        this.notificationService.error("Quantité demandée supérieure au stock disponible", "Erreur");
        return;
      }
      existing.quantite = newQty;
    } else {
      if (qtyToAdd > (this.articleAddBonDto.stock ?? Infinity)) {
        this.notificationService.error("Quantité demandée supérieure au stock disponible", "Erreur");
        return;
      }
      const toPush: ArticleAddBonDto = {
        ...this.articleAddBonDto,
        quantite: qtyToAdd
      };
      this.articlesAddToBon.push(toPush);
      this.notificationService.success("Article ajouté",'Succès');
    }

    await this.recalculateTotal();
    console.log("Articles à ajoutés", this.articlesAddToBon);
  }

  protected async onRemiseChange(): Promise<void> {
    await this.recalculateTotal();
  }

  protected async supprimerArticle(cod: number): Promise<void> {
    const found = this.articlesAddToBon.find(a => a.cod === cod);
    if (!found) {
      console.warn('Article à supprimer non trouvé:', cod);
    }

    this.articlesAddToBon = this.articlesAddToBon.filter(a => a.cod !== cod);
    await this.recalculateTotal();
    console.log("Articles après suppression:", this.articlesAddToBon);
  }

  protected async createBonVente() {
    if (!this.bonVenteDto) {
      this.bonVenteDto = {} as BonAchatVenteDto;
    }

    // Préparer le DTO
    this.bonVenteDto.idTier = Number(this.selectedClientId ?? -1);
    this.bonVenteDto.idUser = Number(this.userId ?? -1);
    this.bonVenteDto.espece = this.toNumber(this.espece);
    this.bonVenteDto.cheque = this.toNumber(this.cheque);

    this.bonVenteDto.detCheque = this.detailsCheque ?? '';
    this.bonVenteDto.datBon = this.selectedDate ? `${this.selectedDate}T00:00:00` : undefined;
    this.bonVenteDto.remis = this.toNumber(this.remiseTotal);
    this.bonVenteDto.articles = this.articlesAddToBon.map(a => ({
      cod: a.cod,
      ref: a.ref,
      designation: a.designation,
      choix: a.choix,
      prix: Number(a.prix ?? 0),
      prixAchat: Number(a.prixAchat ?? 0),
      prixMin: Number(a.prixMin ?? 0),
      quantite: Number(a.quantite ?? 0),
      remisUni: a.remisUni == null ? undefined : Number(a.remisUni),
      stock: Number(a.stock ?? 0)
    }));

    if (!this.selectedSerie || this.selectedSerie.trim() === "") {
      this.selectedSerie = "";
    }

    console.log("Bon Vente Avant:",this.bonVenteDto);

    try {
      const result = await firstValueFrom(
        this.selectedSerie
          ? this.bonVenteService.updateBonVente(this.bonVenteDto, this.selectedSerie)
          : this.bonVenteService.createBonVente(this.bonVenteDto)
      );

      this.bonVenteDto = result ?? ({} as BonAchatVenteDto);
      this.selectedSerie = this.bonVenteDto.serie ?? "";
      this.selectedDate = result.datBon
        ? new Date(result.datBon).toISOString().split('T')[0]
        : new Date().toISOString().split('T')[0];
      this.username=result.nomUser ?? "";

      const message = this.selectedSerie
        ? "BonVente mis à jour avec succès"
        : "BonVente créé avec succès";

      this.notificationService.success(message, "Succès");
      await this.getTierSolde(result.idTier ?? -1);
      this.selectedSerie=result.serie ?? "";
      console.log("Bon Vente Après:",result);
  await this.getBonVente();
      await this.saveAllDataToDb();
    } catch (err: any) {
      console.error('Erreur create/update BonVente (frontend):', err);
      this.notificationService.error(err?.error?.message, 'Erreur');
    }
  }

  protected async supprimer() {
    if (!this.selectedSerie) {
      this.notificationService.error("Sélectionner une série", 'Erreur');
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer ce bon ?')) {
      const serie = this.selectedSerie;
      try {
        await firstValueFrom(this.bonVenteService.annuler(serie));
        this.notificationService.success("Bon N° " + serie + " est supprimé", 'Succès');
        await this.resetValues();
      } catch (err: any) {
        this.notificationService.error(err?.error?.message ?? 'Erreur');
      }
    }
  }

  // NOUVELLES MÉTHODES POUR GÉRER TOUTES LES DONNÉES EN LOCALSTORAGE

  /**
   * Sauvegarde toutes les données importantes du composant en localStorage
   */
  private async saveAllDataToDb(): Promise<void> {
    try {
      const dataToSave: BonAchatVenteDto = {
        serie: this.selectedSerie,
        idTier: this.selectedClientId,
        articles: this.articlesAddToBon,
        datBon: this.selectedDate,
        nomUser: this.username,
        espece: this.espece,
        cheque: this.cheque,
        credit: this.credit,
        detCheque: this.detailsCheque,
        montant: this.totalVenteAvecRemise,
        montantSansRemise: this.totalVenteBrute,
        remis: this.remiseGlobale
      };

      await this.indexedDbService.saveBon(dataToSave);
      console.log('Données sauvegardées dans IndexedDB', dataToSave);
    } catch (error) {
      console.error('Erreur sauvegarde IndexedDB', error);
    }
  }
  async vider(){
    await this.resetValues();
    await this.clearDbData();
  }
  /**
   * Charge toutes les données sauvegardées depuis localStorage
   */
  private async loadAllDataFromDb(): Promise<void> {
    try {
      const data = await this.indexedDbService.loadBon();
      if (data) {
        this.selectedSerie = data.serie || "";
        this.selectedClientId = data.idTier || -1;
        this.articlesAddToBon = data.articles || [];
        this.selectedDate = data.datBon || '';
        this.username=data.nomUser|| '';
        this.espece = data.espece || 0;
        this.cheque = data.cheque || 0;
        this.credit = data.credit || 0;
        this.detailsCheque = data.detCheque || '';
        this.totalVenteAvecRemise = data.montant || 0;
        this.totalVenteBrute = data.montantSansRemise || 0;
        this.remiseGlobale = this.toNumber(data.remis);
        console.log('Données chargées depuis IndexedDB', data);

        if (this.articlesAddToBon.length > 0) await this.recalculateTotal();
        if (this.selectedClientId && this.selectedClientId !== -1) await this.getTierSolde(this.selectedClientId);
        if (this.selectedDesignation) await this.getChoixByDesignation(this.selectedDesignation);
      }
    } catch (error) {
      console.error('Erreur chargement IndexedDB', error);
    }
  }

  /**
   * Efface toutes les données sauvegardées en localStorage
   */
  public async clearDbData(): Promise<void> {
    try {
      await this.indexedDbService.clear();
      console.log('IndexedDB effacée');
    } catch (error) {
      console.error('Erreur effacement IndexedDB', error);
    }
  }

  /**
   * Méthode pour restaurer les données depuis une sauvegarde (peut être appelée depuis le template)
   */

  private toNumber(v: any): number {
    if (v === null || v === undefined || v === '') return 0;
    const n = Number(v);
    return isNaN(n) ? 0 : n;
  }
  protected async onRemiseTotalChange(value: any): Promise<void> {
    this.remiseTotal = this.toNumber(value); // conversion obligatoire
    await this.recalculateTotal();
  }



 protected downloadBonVentePdf() {

    if(!this.selectedSerie || this.selectedSerie.trim()==""){
      this.notificationService.error("Sélectionner une série",'Erreur');
      return;
    }

    this.bonVenteService
      .downloadBonVente(
        this.selectedUser.cod ?? -1,
        this.selectedSerie
      )
      .subscribe({
        next: (pdf: Blob) => {
          const fileName = `Bon_Vente_${this.selectedSerie}.pdf`; // nom voulu
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

}
