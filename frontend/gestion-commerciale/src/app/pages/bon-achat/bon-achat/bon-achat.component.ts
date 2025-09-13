import {Component, OnInit} from '@angular/core';
import {BonAchatVenteService} from "../../../services/bon-achat-vente/bon-achat-vente.service";
import {ArticleAddBonDto, BonAchatVenteDto, TierDto, UserDto} from "../../../api-client";
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
import {selectCurrentUser} from "../../../../store/currentUser/currentUser.selectors";

@Component({
  selector: 'app-bon-achat',
  templateUrl: './bon-achat.component.html',
  styleUrls: ['./bon-achat.component.scss']
})
export class BonAchatComponent implements OnInit {

  selectedAchatSerie: string = "";
  allSeris: string[] = [];
  userId: number | undefined;
  selectedFournisseurId: number = -1;
  fournisseurs$: Observable<TierDto[]>;
  currentUser$: Observable<UserDto|null>;
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
  remisSurBon: number = 0;
  remiseUnitairesTotal: number = 0;
  detailsCheque: string = "";
  isSerieDisabled: boolean = true;
  faX = faTimes;
  selectedUserCod: number = 1;
  username: string = "";
  maxDate: string = '';
  designations: string[] = [];
  choix: string[] = [];
  articleAddBonDto: ArticleAddBonDto = {};
  bonAchatDto: BonAchatVenteDto = {} as BonAchatVenteDto;
  remiseGlobale: number = 0;

  // Flag pour éviter les chargements multiples
  private isInitializing: boolean = false;

  public constructor(
    private articleService: ArticleService,
    private dettesService: DettesService,
    private store: Store<AppState>,
    private bonAchatService: BonAchatVenteService,
    private notificationService: NotificationService,
    private indexedDbService: IndexedBonAchatService
  ) {
    this.fournisseurs$ = this.store.select(selectFournisseursList)
    this.currentUser$ = this.store.select(selectCurrentUser);

    // Amélioration de la souscription à l'utilisateur courant
    this.currentUser$.subscribe(user => {
      console.log('Current user changed:', user);
      this.userId = user?.cod;

      // Si on a un userId et une série sélectionnée, recharger le bon
      if (this.userId && this.selectedAchatSerie && !this.isInitializing) {
        this.getBonAchat();
      }
    });

    this.fournisseurs$.subscribe(fournisseurs => {
      console.log("All Fournisseurs:", fournisseurs)
    });
  }

  async ngOnInit() {
    this.isInitializing = true;

    try {
      await this.loadAllDataFromDb();

      await this.getAllBonASeris();
      await this.getAllDesignations();
      this.initializeDateLimits();

      this.store.dispatch(loadFournisseurs());


      // Ne définir la date d'aujourd'hui que si aucune date n'est chargée
      if (!this.selectedDate) {
        await this.setTodayAsDefault();
      }
    } catch (error) {
      console.error('Erreur lors de l\'initialisation:', error);
    } finally {
      this.isInitializing = false;
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
    this.remisSurBon = 0;
    this.remiseUnitairesTotal = 0;
    this.remiseGlobale = 0;
    this.articleAddBonDto = {};
    this.username = "";
    this.choix = [];

    await this.setTodayAsDefault();
    await this.saveAllDataToDb();
  }

  async toggleSerie() {
    this.isSerieDisabled = !this.isSerieDisabled;

    if (this.isSerieDisabled) {
      // Si on désactive, reset complet
      await this.resetValues();
    } else {
      // Si on active et qu'il y a une série sélectionnée, charger le bon
      if (this.selectedAchatSerie && this.userId) {
        await this.getBonAchat();
      }
    }

    await this.saveAllDataToDb();
  }

  protected async getTierSolde(selectedFournisseurId: number) {
    if (selectedFournisseurId === -1) {
      this.soldeClient = 0;
      await this.saveAllDataToDb();
      return;
    }

    try {
      const result = await firstValueFrom(this.dettesService.getSoldeByTierId(selectedFournisseurId));
      this.soldeClient = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors du chargement du solde:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors du chargement du solde', 'Erreur');
      this.soldeClient = 0;
    }
  }

  private async getAllBonASeris() {
    try {
      const data = await firstValueFrom(this.bonAchatService.getAllBonsAchatSeris());
      this.allSeris = data || [];
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors du chargement des séries:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors du chargement des séries', 'Erreur');
      this.allSeris = [];
    }
  }

  private initializeDateLimits(): void {
    const today = new Date();
    this.maxDate = this.formatDateForInput(today);
  }

  private async getChoixByDesignation(designation: string) {
    if (!designation) {
      this.choix = [];
      return;
    }

    try {
      const result = await firstValueFrom(this.articleService.getChoixByDes(designation));
      this.choix = result || [];
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors du chargement des choix:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors du chargement des choix', 'Erreur');
      this.choix = [];
    }
  }

  async onDesignationChange(designation: string) {
    this.selectedDesignation = designation;
    this.selectedChoix = '';
    this.choix = [];

    // Reset article
    this.articleAddBonDto = {};

    if (designation) {
      await this.getChoixByDesignation(designation);
    }

    await this.saveAllDataToDb();
  }

  async onChoixChange(choix: string) {
    this.selectedChoix = choix;

    if (!this.selectedDesignation) {
      this.notificationService.error("Vous devez sélectionner une designation", 'Erreur');
      return;
    }

    if (!choix) {
      this.articleAddBonDto = {};
      await this.saveAllDataToDb();
      return;
    }

    await this.findArticle(this.selectedDesignation, choix);
  }

  private async findArticle(designation: string, choix: string) {
    try {
      const result = await firstValueFrom(this.articleService.findArticleByDesignationAndChoix(designation, choix));
      this.articleAddBonDto = result || {};
      console.log("Article trouvé:", result);
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors de la recherche de l\'article:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors de la recherche de l\'article', 'Erreur');
      this.articleAddBonDto = {};
    }
  }

  private async getAllDesignations() {
    try {
      const result = await firstValueFrom(this.articleService.getAllDesognations());
      this.designations = result || [];
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors du chargement des désignations:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors du chargement des désignations', 'Erreur');
      this.designations = [];
    }
  }

  // NOUVELLE MÉTHODE : onSerieSelectionChange
  async onSerieSelectionChange(selectedSerie: string) {
    console.log('Série sélectionnée:', selectedSerie);
    this.selectedAchatSerie = selectedSerie;

    // Si la série est vide, reset
    if (!selectedSerie) {
      this.bonAchatDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      this.username = "";
      await this.saveAllDataToDb();
      return;
    }

    await this.getBonAchat();
  }

// Dans getBonAchat(), après avoir assigné les valeurs depuis l'API
  protected async getBonAchat() {
    console.log('getBonAchat() appelée - selectedAchatSerie:', this.selectedAchatSerie, 'userId:', this.userId);

    if (!this.selectedAchatSerie) {
      this.bonAchatDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      this.username = "";
      await this.saveAllDataToDb();
      return;
    }

    if (!this.userId) {
      console.log('userId non défini, attente...');
      setTimeout(() => {
        if (this.userId) {
          this.getBonAchat();
        }
      }, 100);
      return;
    }

    try {
      console.log('Appel du service getBonAchat avec userId:', this.userId, 'serie:', this.selectedAchatSerie);

      const result = await firstValueFrom(this.bonAchatService.getBonAchat(this.userId, this.selectedAchatSerie));
      console.log("Bon Achat Result:", result);

      this.bonAchatDto = result || ({} as BonAchatVenteDto);

      this.selectedFournisseurId = result?.idTier ?? -1;
      this.espece = result?.espece ?? 0;
      this.cheque = result?.cheque ?? 0;
      this.userId = result?.idUser ?? this.userId;
      this.username = result?.nomUser ?? "";
      this.detailsCheque = result?.detCheque ?? "";
      this.articlesAddToBon = result?.articles ?? [];
      this.totalVenteBrute = result?.montantSansRemise ?? 0;
      this.totalVenteAvecRemise = result?.montant ?? 0;

      // CORRECTION: Bien récupérer les valeurs depuis l'API
      this.remiseGlobale = this.toNumber(result?.remis ?? 0);
      this.remisSurBon = this.toNumber(result?.remisSurBon ?? 0);

      this.selectedDate = result?.datBon
        ? new Date(new Date(result.datBon).setDate(new Date(result.datBon).getDate() + 1))
          .toISOString()
          .split('T')[0]
        : new Date().toISOString().split('T')[0];

      console.log("Valeurs récupérées - remiseGlobale:", this.remiseGlobale, "remisSurBon:", this.remisSurBon);

      // Charger le solde fournisseur si nécessaire
      if (this.selectedFournisseurId !== -1) {
        await this.getTierSolde(this.selectedFournisseurId);
      }

      // CORRECTION: Calculer seulement les remises unitaires et le crédit
      // Ne pas recalculer les remises qui viennent de l'API
      this.calculateRemiseUnitairesTotal();
      this.calculateCredit();

      await this.saveAllDataToDb();

    } catch (error: any) {
      console.error('Erreur dans getBonAchat:', error);
      this.notificationService.error(error?.error?.message || "Erreur lors du chargement du bon", "Erreur");
    }
  }

// NOUVELLE MÉTHODE: Calculer seulement les remises unitaires
  private calculateRemiseUnitairesTotal(): void {
    this.remiseUnitairesTotal = this.articlesAddToBon.reduce((sum, a) => {
      const rUnit = Number(a.remisUni ?? 0);
      const prix = Number(a.prixAchat ?? 0);
      const qte = Number(a.quantite ?? 0);
      const rUnitCapped = Math.max(0, Math.min(rUnit, prix));
      return sum + (rUnitCapped * qte);
    }, 0);
    this.remiseUnitairesTotal = this.round2(this.remiseUnitairesTotal);
  }

// NOUVELLE MÉTHODE: Calculer seulement le crédit
  private calculateCredit(): void {
    this.credit = this.round2(
      (this.toNumber(this.espece) + this.toNumber(this.cheque)) - this.totalVenteAvecRemise
    );
  }

// MÉTHODE MODIFIÉE: recalculateTotal pour distinguer les cas
  protected async recalculateTotal(remisSurBonInput?: number, isFromApiLoad: boolean = false): Promise<void> {
    const totalBrutInitial = this.articlesAddToBon.reduce((sum, a) => {
      const prix = Number(a.prixAchat ?? 0);
      const qte = Number(a.quantite ?? 0);
      return sum + (prix * qte);
    }, 0);

    this.calculateRemiseUnitairesTotal();

    // Si les données viennent de l'API, ne pas recalculer les remises
    if (isFromApiLoad) {
      this.totalVenteBrute = this.round2(totalBrutInitial);
      this.calculateCredit();
      await this.saveAllDataToDb();
      return;
    }

    // Logique pour les modifications manuelles
    if (remisSurBonInput !== undefined && remisSurBonInput !== null) {
      this.remisSurBon = this.toNumber(remisSurBonInput);
    } else {
      // Lors d'ajout/suppression d'articles, recalculer remisSurBon
      this.remisSurBon = this.round2(this.remiseGlobale + this.remiseUnitairesTotal);
    }

    if (this.remisSurBon < 0) this.remisSurBon = 0;

    this.totalVenteBrute = this.round2(totalBrutInitial);
    this.totalVenteAvecRemise = this.round2(this.totalVenteBrute - this.remisSurBon);

    if (this.totalVenteAvecRemise < 0) this.totalVenteAvecRemise = 0;

    this.calculateCredit();

    console.log({
      totalBrutInitial: this.totalVenteBrute,
      remiseUnitaires: this.remiseUnitairesTotal,
      remisSurBon: this.remisSurBon,
      remiseGlobale: this.remiseGlobale,
      totalVenteAvecRemise: this.totalVenteAvecRemise,
      credit: this.credit,
      remisSurBonInput: remisSurBonInput,
      isFromApiLoad: isFromApiLoad
    });

    await this.saveAllDataToDb();
  }

// Mettre à jour la méthode pour indiquer que c'est un chargement API dans getBonAchat
// Remplacer l'appel dans getBonAchat par :
// await this.recalculateTotal(undefined, true);

// Pour les autres cas (ajout d'article, modification), garder :
// await this.recalculateTotal();
  private formatDateForInput(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private async setTodayAsDefault(): Promise<void> {
    const today = new Date();
    this.selectedDate = this.formatDateForInput(today);
    await this.saveAllDataToDb();
  }


  protected async onRemisSurBonChange(value: any): Promise<void> {
    const inputValue = this.toNumber(value);
    await this.recalculateTotal(inputValue);
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
      // Si l'article existe déjà, additionner les quantités
      existing.quantite = (existing.quantite ?? 0) + qtyToAdd;
    } else {
      // Sinon, ajouter un nouvel article
      const toPush: ArticleAddBonDto = {
        ...this.articleAddBonDto,
        quantite: qtyToAdd
      };
      this.articlesAddToBon.push(toPush);
    }

    this.notificationService.success("Article ajouté", 'Succès');

    // Reset la sélection d'article
    this.resetArticleSelection();
    await this.recalculateTotal();
    console.log("Articles ajoutés", this.articlesAddToBon);
  }

  protected async supprimerArticle(cod: number): Promise<void> {
    const found = this.articlesAddToBon.find(a => a.cod === cod);
    if (!found) {
      console.warn('Article à supprimer non trouvé:', cod);
      return;
    }

    this.articlesAddToBon = this.articlesAddToBon.filter(a => a.cod !== cod);
    await this.recalculateTotal();
    console.log("Articles après suppression:", this.articlesAddToBon);
  }

  protected async createBonAchat() {
    if (!this.validateBon()) {
      return;
    }

    if (!this.bonAchatDto) {
      this.bonAchatDto = {} as BonAchatVenteDto;
    }

    // Préparer le DTO
    this.bonAchatDto.idTier = Number(this.selectedFournisseurId);
    this.bonAchatDto.idUser = Number(this.userId ?? -1);
    this.bonAchatDto.espece = this.toNumber(this.espece);
    this.bonAchatDto.cheque = this.toNumber(this.cheque);
    this.bonAchatDto.detCheque = this.detailsCheque ?? '';
    this.bonAchatDto.remis = this.toNumber(this.remiseGlobale);
    this.bonAchatDto.remisSurBon = this.toNumber(this.remisSurBon);
    this.bonAchatDto.datBon = this.createDateForServer(this.selectedDate);
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

    const isUpdate = this.selectedAchatSerie && this.selectedAchatSerie.trim() !== "";

    console.log("Bon Achat Avant:", this.bonAchatDto);

    try {
      const result = await firstValueFrom(
        isUpdate
          ? this.bonAchatService.updateBonAchat(this.bonAchatDto, this.selectedAchatSerie)
          : this.bonAchatService.createBonAchat(this.bonAchatDto)
      );

      this.bonAchatDto = result ?? ({} as BonAchatVenteDto);
      this.selectedAchatSerie = result?.serie ?? "";

      this.selectedDate = result?.datBon
        ? new Date(new Date(result.datBon).setDate(new Date(result.datBon).getDate() + 1))
          .toISOString()
          .split('T')[0]
        : new Date().toISOString().split('T')[0];
      this.username = result?.nomUser ?? "";

      const message = isUpdate
        ? "Bon d'Achat mis à jour avec succès"
        : "Bon d'Achat créé avec succès";

      this.notificationService.success(message, "Succès");

      if (result?.idTier) {
        await this.getTierSolde(result.idTier);
      }

      console.log("Bon Achat Après:", result);

      // Recharger les séries si c'était une création
        await this.getAllBonASeris();


      await this.saveAllDataToDb();

    } catch (err: any) {
      console.error('Erreur create/update BonAchat (frontend):', err);
      this.notificationService.error(err?.error?.message || 'Erreur lors de la sauvegarde', 'Erreur');
    }
  }

  private createDateForServer(dateString: string): string | undefined {
    if (!dateString) return undefined;

    const datePattern = /^\d{4}-\d{2}-\d{2}$/;
    if (datePattern.test(dateString)) {
      return `${dateString}T00:00:00`;
    }

    return undefined;
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

        // Recharger les séries
        await this.getAllBonASeris();
        await this.resetValues();

      } catch (err: any) {
        console.error('Erreur suppression:', err);
        this.notificationService.error(err?.error?.message ?? 'Erreur lors de la suppression', 'Erreur');
      }
    }
  }

   async saveAllDataToDb(): Promise<void> {
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
        remis: this.remiseGlobale,
        remisSurBon: this.remisSurBon,
        // Ajouter des données supplémentaires
        idUser: this.userId
      };

      await this.indexedDbService.saveBon(dataToSave);
      console.log('Données sauvegardées dans IndexedDB', dataToSave);
    } catch (error) {
      console.error('Erreur sauvegarde IndexedDB', error);
    }
  }

  private async loadAllDataFromDb(): Promise<void> {
    try {
      const data = await this.indexedDbService.loadBon();
      if (data) {
        console.log('Données chargées depuis IndexedDB', data);

        this.selectedAchatSerie = data.serie || "";
        this.selectedFournisseurId = data.idTier || -1;
        this.articlesAddToBon = data.articles || [];
        this.selectedDate = data.datBon || '';
        this.username = data.nomUser || '';
        this.espece = data.espece || 0;
        this.cheque = data.cheque || 0;
        this.credit = data.credit || 0;
        this.detailsCheque = data.detCheque || '';
        this.totalVenteAvecRemise = data.montant || 0;
        this.totalVenteBrute = data.montantSansRemise || 0;
        this.remiseGlobale = data.remis || 0;
        this.remisSurBon = data.remisSurBon || 0;

        // CORRECTION: Gérer l'état du champ série
        if (this.selectedAchatSerie) {
          this.isSerieDisabled = false; // Activer si une série est sauvegardée
        }

        // Recalculer si nécessaire
        if (this.articlesAddToBon.length > 0) {
          await this.recalculateTotal();
        }

        // Charger le solde fournisseur si nécessaire
        if (this.selectedFournisseurId && this.selectedFournisseurId !== -1) {
          await this.getTierSolde(this.selectedFournisseurId);
        }

        // Charger les choix si une désignation est sélectionnée
        if (this.selectedDesignation) {
          await this.getChoixByDesignation(this.selectedDesignation);
        }
      }
    } catch (error) {
      console.error('Erreur chargement IndexedDB', error);
    }
  }

  async vider() {
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

  private toNumber(v: any): number {
    if (v === null || v === undefined || v === '') return 0;
    const n = Number(v);
    return isNaN(n) ? 0 : n;
  }

  protected downloadBonAchatPdf() {
    if (!this.selectedAchatSerie || this.selectedAchatSerie.trim() == "") {
      this.notificationService.error("Sélectionner une série", 'Erreur');
      return;
    }

    this.bonAchatService
      .downloadBonAchat(this.selectedAchatSerie)
      .subscribe({
        next: (pdf: Blob) => {
          const fileName = `Bon_Achat_${this.selectedAchatSerie}.pdf`;
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
          console.error('Erreur téléchargement PDF:', err);
          this.notificationService.error(err.error?.message || 'Erreur lors du téléchargement', 'Erreur');
        },
      });
  }

  /**
   * TrackBy function pour optimiser ngFor
   */
  trackByArticleCode(index: number, article: ArticleAddBonDto): any {
    return article.cod ? article.cod : index;
  }

  /**
   * Méthode utilitaire pour valider le bon avant sauvegarde
   */
  private validateBon(): boolean {
    if (this.selectedFournisseurId === -1) {
      this.notificationService.error("Veuillez sélectionner un fournisseur", "Erreur");
      return false;
    }

    if (this.articlesAddToBon.length === 0) {
      this.notificationService.error("Veuillez ajouter au moins un article", "Erreur");
      return false;
    }

    if (!this.selectedDate) {
      this.notificationService.error("Veuillez sélectionner une date", "Erreur");
      return false;
    }

    return true;
  }

  /**
   * Méthode pour réinitialiser la sélection d'article
   */
  private resetArticleSelection(): void {
    this.articleAddBonDto = {};
    this.selectedDesignation = '';
    this.selectedChoix = '';
    this.choix = [];
  }

  /**
   * Méthode pour calculer le total d'un article
   */
  calculateArticleTotal(article: ArticleAddBonDto): number {
    const prix = Number(article.prixAchat || 0);
    const quantite = Number(article.quantite || 1);
    const remiseUni = Number(article.remisUni || 0);

    return this.round2((prix - Math.min(remiseUni, prix)) * quantite);
  }
}
