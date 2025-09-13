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
import {selectCurrentUser} from "../../../store/currentUser/currentUser.selectors";

@Component({
  selector: 'app-bon-vente',
  templateUrl: './bon-vente.component.html',
  styleUrls: ['./bon-vente.component.scss']
})
export class BonVenteComponent implements OnInit {

  selectedSerie: string = "";
  allSeris: string[] = [];
  userId: number | undefined;
  selectedClientId: number = -1;
  clients$: Observable<TierDto[]>;
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
  remiseTotal: number = 0;
  remisSurBon: number = 0;
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

  // Flag pour éviter les chargements multiples
  private isInitializing: boolean = false;

  public constructor(
    private articleService: ArticleService,
    private dettesService: DettesService,
    private store: Store<AppState>,
    private bonVenteService: BonAchatVenteService,
    private notificationService: NotificationService,
    private indexedDbService: IndexedDBService
  ) {
    this.clients$ = this.store.select(selectClientsList)
    this.currentUser$ = this.store.select(selectCurrentUser)

    // Amélioration de la souscription à l'utilisateur courant
    this.currentUser$.subscribe(user => {
      console.log('Current user changed:', user);
      this.userId = user?.cod;

      // Si on a un userId et une série sélectionnée, recharger le bon
      if (this.userId && this.selectedSerie && !this.isInitializing) {
        this.getBonVente();
      }
    });

    this.clients$.subscribe(clients => {
      console.log("All Clients:", clients)
    });
  }

  async ngOnInit() {
    this.isInitializing = true;
    await this.loadAllDataFromDb();

    try {
      // Charger les données de base
      await this.getAllBonVSeris();
      await this.getAllDesignations();
      this.initializeDateLimits();

      // Dispatch pour charger les clients
      this.store.dispatch(loadClients());

      // Charger les données sauvegardées

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
    this.bonVenteDto = {} as BonAchatVenteDto;
    this.articlesAddToBon = [];
    this.selectedClientId = -1;
    this.selectedSerie = "";
    this.selectedDesignation = '';
    this.selectedChoix = '';
    this.remiseTotal = 0;
    this.remiseGlobale = 0;
    this.remiseUnitairesTotal = 0;
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
      if (this.selectedSerie && this.userId) {
        await this.getBonVente();
      }
    }

    await this.saveAllDataToDb();
  }

  protected async getTierSolde(selectedClientId: number) {
    if (selectedClientId === -1) {
      this.soldeClient = 0;
      await this.saveAllDataToDb();
      return;
    }

    try {
      const result = await firstValueFrom(this.dettesService.getSoldeByTierId(selectedClientId));
      this.soldeClient = result;
      await this.saveAllDataToDb();
    } catch (error: any) {
      console.error('Erreur lors du chargement du solde:', error);
      this.notificationService.error(error?.error?.message || 'Erreur lors du chargement du solde', 'Erreur');
      this.soldeClient = 0;
    }
  }

  private async getAllBonVSeris() {
    try {
      const data = await firstValueFrom(this.bonVenteService.getAllBonsVenteSeris());
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

  async onSerieSelectionChange(selectedSerie: string) {
    console.log('Série sélectionnée:', selectedSerie);
    this.selectedSerie = selectedSerie;

    // Si la série est vide, reset
    if (!selectedSerie) {
      this.bonVenteDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      this.username = "";
      await this.saveAllDataToDb();
      return;
    }

    await this.getBonVente();
  }

  protected async getBonVente() {
    console.log('getBonVente() appelée - selectedSerie:', this.selectedSerie, 'userId:', this.userId);

    if (!this.selectedSerie) {
      this.bonVenteDto = {} as BonAchatVenteDto;
      this.articlesAddToBon = [];
      this.username = "";
      await this.saveAllDataToDb();
      return;
    }

    if (!this.userId) {
      console.log('userId non défini, attente...');
      setTimeout(() => {
        if (this.userId) {
          this.getBonVente();
        }
      }, 100);
      return;
    }

    try {
      console.log('Appel du service getBonVente avec userId:', this.userId, 'serie:', this.selectedSerie);

      const result = await firstValueFrom(this.bonVenteService.getBonAchat(this.userId, this.selectedSerie));
      console.log("Bon Vente Result:", result);

      // CORRECTION: bonVenteDto au lieu de bonAchatDto
      this.bonVenteDto = result || ({} as BonAchatVenteDto);

      this.selectedClientId = result?.idTier ?? -1;
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
      // Pour la compatibilité avec l'ancien système
      this.remiseTotal = this.toNumber(result?.remis ?? 0);

      this.selectedDate = result?.datBon
        ? new Date(new Date(result.datBon).setDate(new Date(result.datBon).getDate() + 1))
          .toISOString()
          .split('T')[0]
        : new Date().toISOString().split('T')[0];

      console.log("Valeurs récupérées - remiseGlobale:", this.remiseGlobale, "remisSurBon:", this.remisSurBon);

      // Charger le solde client si nécessaire
      if (this.selectedClientId !== -1) {
        await this.getTierSolde(this.selectedClientId);
      }

      // CORRECTION: Ne pas recalculer les remises, juste calculer les remises unitaires et le crédit
      this.calculateRemiseUnitairesTotal();
      this.calculateCredit();

      await this.saveAllDataToDb();

    } catch (error: any) {
      console.error('Erreur dans getBonVente:', error);
      this.notificationService.error(error?.error?.message || "Erreur lors du chargement du bon", "Erreur");
    }
  }

  // CORRECTION 3: Ajouter les méthodes manquantes
  private calculateRemiseUnitairesTotal(): void {
    this.remiseUnitairesTotal = this.articlesAddToBon.reduce((sum, a) => {
      const rUnit = Number(a.remisUni ?? 0);
      const prix = Number(a.prix ?? 0);
      const qte = Number(a.quantite ?? 0);
      const rUnitCapped = Math.max(0, Math.min(rUnit, prix));
      return sum + (rUnitCapped * qte);
    }, 0);
    this.remiseUnitairesTotal = this.round2(this.remiseUnitairesTotal);
  }

  private calculateCredit(): void {
    this.credit = this.round2(
      (this.toNumber(this.espece) + this.toNumber(this.cheque)) - this.totalVenteAvecRemise
    );
  }

  // CORRECTION 4: Modifier recalculateTotal pour éviter d'écraser lors du chargement API
  protected async recalculateTotal(skipRemiseCalculation: boolean = false): Promise<void> {
    const totalBrutInitial = this.articlesAddToBon.reduce((sum, a) => {
      const prix = Number(a.prix ?? 0);
      const qte = Number(a.quantite ?? 0);
      return sum + (prix * qte);
    }, 0);

    this.calculateRemiseUnitairesTotal();

    // Si on doit ignorer le recalcul des remises (lors du chargement API)
    if (skipRemiseCalculation) {
      this.totalVenteBrute = this.round2(totalBrutInitial);
      this.calculateCredit();
      await this.saveAllDataToDb();
      return;
    }

    // Logique normale pour les modifications manuelles
    // SIMPLE : remiseGlobale = remiseTotal + remiseUnitairesTotal
    this.remiseGlobale = this.round2(this.toNumber(this.remiseTotal) + this.remiseUnitairesTotal);
    this.remisSurBon = this.remiseGlobale; // Pour compatibilité

    this.totalVenteBrute = this.round2(totalBrutInitial);
    this.totalVenteAvecRemise = this.round2(this.totalVenteBrute - this.remiseGlobale);

    if (this.totalVenteAvecRemise < 0) this.totalVenteAvecRemise = 0;

    this.calculateCredit();

    console.log({
      totalBrutInitial: this.totalVenteBrute,
      remiseUnitaires: this.remiseUnitairesTotal,
      remiseTotale: this.remiseTotal, // Saisie utilisateur
      remiseGlobale: this.remiseGlobale, // Calculée
      remisSurBon: this.remisSurBon,
      totalVenteAvecRemise: this.totalVenteAvecRemise,
      credit: this.credit,
      skipRemiseCalculation: skipRemiseCalculation
    });

    await this.saveAllDataToDb();
  }

  // CORRECTION 5: Modifier loadAllDataFromDb pour éviter de recalculer lors du chargement
  private async loadAllDataFromDb(): Promise<void> {
    try {
      const data = await this.indexedDbService.loadBon();
      if (data) {
        console.log('Données chargées depuis IndexedDB', data);

        this.selectedSerie = data.serie || "";
        this.selectedClientId = data.idTier || -1;
        this.articlesAddToBon = data.articles || [];
        this.selectedDate = data.datBon || '';
        this.username = data.nomUser || '';
        this.espece = data.espece || 0;
        this.cheque = data.cheque || 0;
        this.credit = data.credit || 0;
        this.detailsCheque = data.detCheque || '';
        this.totalVenteAvecRemise = data.montant || 0;
        this.totalVenteBrute = data.montantSansRemise || 0;
        this.remiseGlobale = this.toNumber(data.remis);
        this.remiseTotal = this.toNumber(data.remis); // Sync
        this.remisSurBon = this.toNumber(data.remisSurBon || data.remis); // Support des deux formats

        // CORRECTION: Gérer l'état du champ série
        if (this.selectedSerie) {
          this.isSerieDisabled = false; // Activer si une série est sauvegardée
        }

        // CORRECTION: Ne pas recalculer si on a déjà des totaux chargés, juste calculer le crédit
        if (this.articlesAddToBon.length > 0) {
          if (this.totalVenteAvecRemise > 0) {
            // Données déjà calculées, juste mettre à jour le crédit
            this.calculateCredit();
          } else {
            // Pas de totaux sauvegardés, recalculer
            await this.recalculateTotal();
          }
        }

        // Charger le solde client si nécessaire
        if (this.selectedClientId && this.selectedClientId !== -1) {
          await this.getTierSolde(this.selectedClientId);
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

  // CORRECTION 6: Mettre à jour saveAllDataToDb pour inclure remisSurBon
  async saveAllDataToDb(): Promise<void> {
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
        remis: this.remiseGlobale,
        remisSurBon: this.remisSurBon, // Ajouter cette propriété
        // Ajouter des données supplémentaires
        idUser: this.userId
      };

      await this.indexedDbService.saveBon(dataToSave);
      console.log('Données sauvegardées dans IndexedDB', dataToSave);
    } catch (error) {
      console.error('Erreur sauvegarde IndexedDB', error);
    }
  }

  // CORRECTION 7: Mettre à jour createBonVente pour envoyer remisSurBon
  protected async createBonVente() {
    if (!this.validateBon()) {
      return;
    }

    if (!this.bonVenteDto) {
      this.bonVenteDto = {} as BonAchatVenteDto;
    }

    // Préparer le DTO
    this.bonVenteDto.idTier = Number(this.selectedClientId);
    this.bonVenteDto.idUser = Number(this.userId ?? -1);
    this.bonVenteDto.espece = this.toNumber(this.espece);
    this.bonVenteDto.cheque = this.toNumber(this.cheque);
    this.bonVenteDto.detCheque = this.detailsCheque ?? '';
    this.bonVenteDto.datBon = this.selectedDate ? `${this.selectedDate}T00:00:00` : undefined;
    this.bonVenteDto.remis = this.toNumber(this.remiseTotal); // Remise saisie par l'utilisateur
    this.bonVenteDto.remisSurBon = this.toNumber(this.remiseGlobale); // Remise totale calculée
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

    const isUpdate = this.selectedSerie && this.selectedSerie.trim() !== "";

    console.log("Bon Vente Avant:", this.bonVenteDto);

    try {
      console.log("Update:", isUpdate);
      const result = await firstValueFrom(
        isUpdate
          ? this.bonVenteService.updateBonVente(this.bonVenteDto, this.selectedSerie)
          : this.bonVenteService.createBonVente(this.bonVenteDto)
      );

      this.bonVenteDto = result ?? ({} as BonAchatVenteDto);
      this.selectedSerie = result?.serie ?? "";
      this.selectedDate = result?.datBon
        ? new Date(new Date(result.datBon).setDate(new Date(result.datBon).getDate() + 1))
          .toISOString()
          .split('T')[0]
        : new Date().toISOString().split('T')[0];
      this.username = result?.nomUser ?? "";

      // Mettre à jour les remises depuis la réponse
      this.remiseGlobale = this.toNumber(result?.remis ?? 0);
      this.remisSurBon = this.toNumber(result?.remisSurBon ?? 0);
      this.remiseTotal = this.toNumber(result?.remis ?? 0);

      console.log("Date bon:", result.datBon);
      const message = isUpdate
        ? "BonVente mis à jour avec succès"
        : "BonVente créé avec succès";

      this.notificationService.success(message, "Succès");

      if (result?.idTier) {
        await this.getTierSolde(result.idTier);
      }

      console.log("Bon Vente Après:", result);

      // Recharger les séries si c'était une création
      await this.getAllBonVSeris();
      await this.saveAllDataToDb();

    } catch (err: any) {
      console.error('Erreur create/update BonVente (frontend):', err);
      this.notificationService.error(err?.error?.message || 'Erreur lors de la sauvegarde', 'Erreur');
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
      this.notificationService.success("Article ajouté", 'Succès');
    }

    // Reset la sélection d'article
    this.resetArticleSelection();
    await this.recalculateTotal();
    console.log("Articles ajoutés", this.articlesAddToBon);
  }

  protected async onRemiseChange(): Promise<void> {
    await this.recalculateTotal();
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

        // Recharger les séries
        await this.getAllBonVSeris();
        await this.resetValues();

      } catch (err: any) {
        console.error('Erreur suppression:', err);
        this.notificationService.error(err?.error?.message ?? 'Erreur lors de la suppression', 'Erreur');
      }
    }
  }

  /**
   * Sauvegarde toutes les données importantes du composant en IndexedDB
   */

  async vider() {
    await this.resetValues();
    await this.clearDbData();
  }

  /**
   * Charge toutes les données sauvegardées depuis IndexedDB
   */

  /**
   * Efface toutes les données sauvegardées en IndexedDB
   */
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

  protected async onRemiseTotalChange(value: any): Promise<void> {
    this.remiseTotal = this.toNumber(value);
    await this.recalculateTotal();
  }

  protected downloadBonVentePdf() {
    if (!this.userId || this.selectedSerie.trim() == "") {
      this.notificationService.error("Sélectionner une série", 'Erreur');
      return;
    }

    this.bonVenteService
      .downloadBonVente(this.selectedSerie)
      .subscribe({
        next: (pdf: Blob) => {
          const fileName = `Bon_Vente_${this.selectedSerie}.pdf`;
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
          this.notificationService.error(err.error || 'Erreur lors du téléchargement', 'Erreur');
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
    if (this.selectedClientId === -1) {
      this.notificationService.error("Veuillez sélectionner un client", "Erreur");
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

    // Vérifier les stocks
    const invalidArticles = this.articlesAddToBon.filter(art =>
      (art.quantite || 0) > (art.stock || 0)
    );
    const isUpdate = this.selectedSerie && this.selectedSerie.trim() !== "";

    if (invalidArticles.length > 0 && !isUpdate) {
      this.notificationService.error(
        `Stock insuffisant pour: ${invalidArticles.map(a => a.designation).join(', ')}`,
        "Erreur"
      );
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
    const prix = Number(article.prix || 0);
    const quantite = Number(article.quantite || 1);
    const remiseUni = Number(article.remisUni || 0);

    return this.round2((prix - Math.min(remiseUni, prix)) * quantite);
  }
}
