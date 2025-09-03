import {Component, OnInit} from '@angular/core';
import {BonAchatVenteService} from "../../../services/bon-achat-vente/bon-achat-vente.service";
import {ArticleAddBonDto, BonAchatVenteDto, TierDto} from "../../../api-client";
import {NotificationService} from "../../../services/notification/notification.service";
import {firstValueFrom, Observable} from "rxjs";
import {Store} from "@ngrx/store";
import {AppState} from "../../../app.state";
import {selectAllUsersState, selectUsers} from "../../../../store/all-users/allUsers.selectors";
import {loadAllUsers} from "../../../../store/all-users/allUsers.actions";
import {ArticleService} from "../../../services/article/article.service";
import {faTimes} from "@fortawesome/free-solid-svg-icons/faTimes";
import {DettesService} from "../../../services/dettes/dettes.service";
import {IndexedBonAchatService} from "../../../services/indexedBonAchat/indexed-bon-achat.service";
import {selectFournisseursList} from "../../../../store/fournisseurs/fournisseurs.selectors";
import {loadFournisseurs} from "../../../../store/fournisseurs/fournisseurs.actions";

@Component({
  selector: 'app-bon-achat',
  templateUrl: './bon-achat.component.html',
  styleUrls: ['./bon-achat.component.scss']
})
export class BonAchatComponent implements OnInit {

  selectedAchatSerie: string = "";
  allSeris: string[] = [];
  userId: number | undefined = 1;
  selectedFournisseurId: number = -1;
  fournisseurs$: Observable<TierDto[]>;
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
  remiseUnitairesTotal: number = 0;
  detailsCheque: string = "";
  isSerieDisabled: boolean = true;
  faX = faTimes;
  selectedUserCod: number = 1;
  username:string = "";
  maxDate: string = '';
  designations: string[] = [];
  choix: string[] = [];
  articleAddBonDto: ArticleAddBonDto = {};
  bonAchatDto: BonAchatVenteDto = {} as BonAchatVenteDto;
  remiseGlobale: number=0;
  // Clé pour localStorage
  //private readonly STORAGE_KEY = 'bonVente_data';

  public constructor(
    private articleService: ArticleService,
    private dettesService: DettesService,
    private store: Store<AppState>,
    private bonAchatService: BonAchatVenteService,
    private notificationService: NotificationService,
    private indexedDbService: IndexedBonAchatService
  ) {
    this.fournisseurs$ = this.store.select(selectFournisseursList)
    this.fournisseurs$.subscribe(fournisseurs => {
      console.log("All Fournisseurs:", fournisseurs)
    });
  }

  async ngOnInit() {
    await this.loadAllDataFromDb(); // Charger toutes les données sauvegardées
    await this.getAllBonASeris();
    this.store.dispatch(loadFournisseurs());
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
    this.soldeClient = 0;
    this.credit = 0;
    this.bonAchatDto = {} as BonAchatVenteDto;
    this.articlesAddToBon = [];
    this.selectedFournisseurId = -1;
    this.selectedAchatSerie = "";
    this.selectedDesignation = '';
    this.selectedChoix = '';
    this.remiseTotal = 0;
    this.remiseUnitairesTotal = 0; // RÉINITIALISER la nouvelle variable
    this.remiseGlobale = 0;
    this.articleAddBonDto = {};
    await this.setTodayAsDefault();
    // Sauvegarder après reset
    await this.saveAllDataToDb();
  }


  async toggleSerie() {
    this.isSerieDisabled = !this.isSerieDisabled;
    if (this.isSerieDisabled) {
      await this.resetValues();
      this.selectedAchatSerie = '';
      this.username="";

    }
    await this.saveAllDataToDb();
  }

  protected async getTierSolde(selectedFournisseurId: number) {
    try {
      const result = await firstValueFrom(this.dettesService.getSoldeByTierId(selectedFournisseurId));
      this.soldeClient = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      this.notificationService.error(error?.error?.message, 'Erreur');
    }
  }

  private async getAllBonASeris() {
    try {
      const data = await firstValueFrom(this.bonAchatService.getAllBonsAchatSeris(this.userId ?? -1));
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

  protected async getBonAchat() {
    if (!this.selectedAchatSerie) {
      this.bonAchatDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      await this.saveAllDataToDb();
      return;
    }

    try {
      const result = await firstValueFrom(this.bonAchatService.getBonAchat(this.userId, this.selectedAchatSerie));
      this.bonAchatDto = result;
      this.espece = result.espece ?? 0;
      this.cheque = result.cheque ?? 0;
      this.selectedFournisseurId = result.idTier ?? -1;
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
      console.log("Bon Vente:", this.bonAchatDto);
      await this.getTierSolde(this.selectedFournisseurId);
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

    // STOCKER la somme des remises unitaires dans la nouvelle variable
    this.remiseUnitairesTotal = this.round2(totalRemiseUnit);

    // Conserver la remiseGlobale (peut être modifiée par l'utilisateur)
    this.remiseGlobale = this.toNumber(this.remiseGlobale);

    // RECALCULER remiseTotal = remiseGlobale - remiseUnitairesTotal
    this.remiseTotal = this.round2(this.remiseGlobale - this.remiseUnitairesTotal);

    // S'assurer que remiseTotal n'est pas négatif
    if (this.remiseTotal < 0) this.remiseTotal = 0;

    this.totalVenteBrute = this.round2(totalBrutInitial);
    this.totalVenteAvecRemise = this.round2(this.totalVenteBrute - this.remiseGlobale);

    if (this.totalVenteAvecRemise < 0) this.totalVenteAvecRemise = 0;

    this.credit = this.round2(
      (this.toNumber(this.espece) + this.toNumber(this.cheque))-this.totalVenteAvecRemise
    );

    console.log({
      totalBrutInitial: this.totalVenteBrute,
      remiseUnitaires: this.remiseUnitairesTotal,
      remiseTotale: this.remiseTotal, // Maintenant calculée
      remiseGlobale: this.remiseGlobale,
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


      const toPush: ArticleAddBonDto = {
        ...this.articleAddBonDto,
        quantite: qtyToAdd
      };
      this.articlesAddToBon.push(toPush);

    this.notificationService.success("Article ajouté",'Succès');
    await this.recalculateTotal();
    console.log("Articles à ajoutés", this.articlesAddToBon);
  }
  protected async onRemiseTotalChange(value: any): Promise<void> {
    this.remiseTotal = this.toNumber(value); // conversion obligatoire
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

  protected async createBonAchat() {
    if (!this.bonAchatDto) {
      this.bonAchatDto = {} as BonAchatVenteDto;
    }

    // Préparer le DTO
    this.bonAchatDto.idTier = Number(this.selectedFournisseurId ?? -1);
    this.bonAchatDto.idUser = Number(this.userId ?? -1);
    this.bonAchatDto.espece = this.toNumber(this.espece);
    this.bonAchatDto.cheque = this.toNumber(this.cheque);
    this.bonAchatDto.detCheque = this.detailsCheque ?? '';
    this.bonAchatDto.remis = this.toNumber(this.remiseTotal);
    this.bonAchatDto.datBon = this.selectedDate ? `${this.selectedDate}T00:00:00` : undefined;
    this.bonAchatDto.articles = this.articlesAddToBon.map(a => ({
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
    console.log("Bon Achat Avant:",this.bonAchatDto);

    if (!this.selectedAchatSerie || this.selectedAchatSerie.trim() === "") {
      this.selectedAchatSerie = "";
    }

    try {
      const result = await firstValueFrom(
        this.selectedAchatSerie
          ? this.bonAchatService.updateBonAchat(this.bonAchatDto, this.selectedAchatSerie)
          : this.bonAchatService.createBonAchat(this.bonAchatDto)
      );

      this.bonAchatDto = result ?? ({} as BonAchatVenteDto);
      this.selectedAchatSerie = this.bonAchatDto.serie ?? "";
      this.selectedDate = result.datBon
        ? new Date(result.datBon).toISOString().split('T')[0]
        : new Date().toISOString().split('T')[0];
      this.username=result.nomUser ?? "";

      const message = this.selectedAchatSerie
        ? "Bon d'Achat mis à jour avec succès"
        : "Bon d'Achat créé avec succès";

      this.notificationService.success(message, "Succès");
      await this.getTierSolde(result.idTier ?? -1);
      await this.saveAllDataToDb();
    } catch (err: any) {
      console.error('Erreur create/update BonVente (frontend):', err);
      this.notificationService.error(err?.error?.message, 'Erreur');
    }
  }

  protected async supprimer() {
    if (!this.selectedAchatSerie) {
      this.notificationService.error("Sélectionner une série", 'Erreur');
      return;
    }

    if (confirm('Êtes-vous sûr de vouloir supprimer ce bon ?')) {
      const serie = this.selectedAchatSerie;
      try {
        await firstValueFrom(this.bonAchatService.annuler(serie));
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
        serie: this.selectedAchatSerie,
        idTier: this.selectedFournisseurId,
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
  }  /**
   * Charge toutes les données sauvegardées depuis localStorage
   */
  private async loadAllDataFromDb(): Promise<void> {
    try {
      const data = await this.indexedDbService.loadBon();
      if (data) {
        this.selectedAchatSerie = data.serie || "";
        this.selectedFournisseurId = data.idTier || -1;
        this.articlesAddToBon = data.articles || [];
        this.selectedDate = data.datBon || '';
        this.username=data.nomUser|| '';
        this.espece = data.espece || 0;
        this.cheque = data.cheque || 0;
        this.credit = data.credit || 0;
        this.detailsCheque = data.detCheque || '';
        this.totalVenteAvecRemise = data.montant || 0;
        this.totalVenteBrute = data.montantSansRemise || 0;
        this.remiseGlobale = data.remis || 0;
        console.log('Données chargées depuis IndexedDB', data);

        if (this.articlesAddToBon.length > 0) await this.recalculateTotal();
        if (this.selectedFournisseurId && this.selectedFournisseurId !== -1) await this.getTierSolde(this.selectedFournisseurId);
        if (this.selectedDesignation) await this.getChoixByDesignation(this.selectedDesignation);
      }
    } catch (error) {
      console.error('Erreur chargement IndexedDB', error);
    }
  }

  /**
   * Efface toutes les données sauvegardées en localStorage
   */
  async vider(){
    await this.resetValues();
    await this.clearDbData();
  }
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

  protected downloadBonAchatPdf() {

    if(!this.selectedAchatSerie || this.selectedAchatSerie.trim()==""){
      this.notificationService.error("Sélectionner une série",'Erreur');
      return;
    }

    this.bonAchatService
      .downloadBonAchat(
        this.selectedUserCod ?? -1,
        this.selectedAchatSerie
      )
      .subscribe({
        next: (pdf: Blob) => {
          const fileName = `Bon_Achat_${this.selectedAchatSerie}.pdf`; // nom voulu
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
