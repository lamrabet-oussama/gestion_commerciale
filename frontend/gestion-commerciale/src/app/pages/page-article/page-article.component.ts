import {Component, OnInit} from '@angular/core';
import {ArticleService} from "../../services/article/article.service";
import {ArticleDto} from "../../api-client";
import {NotificationService} from "../../services/notification/notification.service";
import {faTimes} from "@fortawesome/free-solid-svg-icons/faTimes";
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-page-article',
  templateUrl: './page-article.component.html',
  styleUrls: ['./page-article.component.scss']
})
export class PageArticleComponent implements OnInit {
  familles: string[] = [];
  choix: string[] = [];
  articleDto: ArticleDto = {};
  errorMsg: Array<string> = [];
  message: string = "";
  successMsg = '';
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;
  keyword: string = "";
  resultNum: number = 0;
  refNum: number = 1;

  articles: Array<ArticleDto> = [];
  faX = faTimes;

  constructor(
    private articleService: ArticleService,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    // Initialisation du stock
    this.articleDto.stock = 0;

    // Chargement synchronisé de toutes les données initiales
    this.initializeData();
  }

  private initializeData(): void {
    // Utilisation de forkJoin pour synchroniser tous les appels
    forkJoin({
      familles: this.articleService.findAllFamilles(),
      choix: this.articleService.findAllChoix(),
      totalElements: this.articleService.getTotalElements()
    }).subscribe({
      next: (results) => {
        // Assignation des résultats
        this.familles = results.familles;
        this.choix = results.choix;
        this.articleDto.ref = results.totalElements + 1;

        console.log('Données initiales chargées:');
        console.log('- Familles:', this.familles.length);
        console.log('- Choix:', this.choix.length);
        console.log('- RefNum calculé:', this.refNum);

        // Maintenant charger les articles
        this.getAllArticles(this.currentPage);
      },
      error: (error) => {
        console.error('Erreur lors du chargement des données initiales:', error);
        this.notification.error('Erreur lors du chargement des données', 'Erreur');

        // En cas d'erreur, charger quand même les articles avec refNum par défaut
        this.getAllArticles(this.currentPage);
      }
    });
  }

  private resetMessages() {
    this.errorMsg = [];
    this.message = '';
    this.successMsg = '';
  }

  // Méthode simplifiée et optionnelle pour mettre à jour uniquement refNum
  private updateRefNum(): void {
    this.articleService.getTotalElements().subscribe({
      next: (value) => {
        this.articleDto.ref = value + 1;
        console.log('RefNum mis à jour:', this.refNum);
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour de refNum:', error);
      }
    });
  }

  getAllArticles(page: number): void {
    console.log(`Chargement des articles - Page: ${page}, PageSize: ${this.pageSize}`);

    this.articleService.getPaginatedArticles(page, this.pageSize).subscribe({
      next: (data) => {
        console.log('Réponse complète du service:', data);

        if (data && data.content) {
          this.articles = data.content;
          this.resultNum = data.totalElements ?? 0;
          this.currentPage = data.currentPage ?? page;
          this.totalPages = data.totalPages ?? 0;

          // NE PAS recalculer refNum ici pour éviter les conflits
          // this.refNum = (data.content.length + 1); // ❌ À supprimer

          console.log('Articles assignés:', this.articles);
          console.log('Page courante:', this.currentPage);
          console.log('Total pages:', this.totalPages);
          console.log('Nombre d\'articles:', this.articles.length);
          console.log('RefNum actuel:', this.refNum);
        } else {
          console.warn('Structure de réponse inattendue:', data);
          this.articles = [];
        }
      },
      error: (error) => {
        console.error('Erreur lors du chargement des articles:', error);
        this.articles = [];
        this.notification.error('Erreur lors du chargement des articles', 'Erreur');
      }
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.getAllArticles(page);
    }
  }

  validerArticle(): boolean {
    this.errorMsg = [];
    let valide = true;

    if (!this.articleDto.ref || this.articleDto.ref <= 0) {
      this.errorMsg.push("La référence de l'article est obligatoire.");
      valide = false;
    }

    if (this.articleDto.stock != null && this.articleDto.stock < 0) {
      this.errorMsg.push("Le stock de l'article ne peut pas être négatif.");
      valide = false;
    }

    if (!this.articleDto.prix || this.articleDto.prix <= 0) {
      this.errorMsg.push("Le prix de vente de l'article est obligatoire.");
      valide = false;
    }

    if (!this.articleDto.prixAchat || this.articleDto.prixAchat <= 0) {
      this.errorMsg.push("Le prix d'achat de l'article est obligatoire.");
      valide = false;
    }

    if (!this.articleDto.designation || this.articleDto.designation.trim() === '') {
      this.errorMsg.push("La désignation est obligatoire.");
      valide = false;
    }

    if (!this.articleDto.famille || this.articleDto.famille.trim() === '') {
      this.errorMsg.push("La famille est obligatoire.");
      valide = false;
    }

    if (!this.articleDto.choix || this.articleDto.choix.trim() === '') {
      this.errorMsg.push("Le choix est obligatoire.");
      valide = false;
    }

    return valide;
  }

  validerUpdatedArticle(): boolean {
    this.errorMsg = [];
    let valide = true;

    if (!this.articleDto.cod || this.articleDto.cod <= 0) {
      this.errorMsg.push("Le code de l'article est obligatoire.");
      valide = false;
    }

    console.log("article à modifier:", this.articleDto);

    if (!this.articleDto.designation || this.articleDto.designation.trim() === '') {
      this.errorMsg.push("La désignation est obligatoire.");
      valide = false;
    }

    if (!this.articleDto.choix || this.articleDto.choix.trim() === '') {
      this.errorMsg.push("Le choix est obligatoire.");
      valide = false;
    }

    return valide;
  }

  enregistrerArticle() {
    this.resetMessages();

    if (!this.validerArticle()) {
      this.notification.error("Veuillez remplir tous les champs obligatoires", "Erreur");
      return;
    }

    // S'assurer que la référence est bien assignée

    console.log("articleDto envoyé :", this.articleDto);

    this.articleService.creeArticle(this.articleDto).subscribe({
      next: (data) => {
        this.notification.success('Article ajouté avec succès', 'Succès');
        this.successMsg = 'Article ajouté avec succès';

        setTimeout(() => {
          this.successMsg = '';
        }, 3000);

        // Reset du formulaire
        this.articleDto = { stock: 0 };

        // Mettre à jour refNum pour le prochain article
        this.updateRefNum();
        this.initializeData();
        // Recharger la liste
        this.getAllArticles(this.currentPage);
      },
      error: (error) => {
        this.notification.error(error?.error?.message || 'Erreur lors de la création');
        console.error('Erreur lors de la création:', error);
        this.errorMsg = error?.error?.errors || ['Erreur lors de la création de l\'article'];
        this.message = error?.error?.message || '';
      }
    });
  }

  modifierArticle() {
    this.resetMessages();

    if (!this.validerUpdatedArticle()) {
      this.notification.error("Veuillez remplir tous les champs obligatoires", "Erreur");
      return;
    }

    console.log("articleDto envoyé :", this.articleDto);

    this.articleService.modifierArticle(this.articleDto.cod ?? 0, this.articleDto).subscribe({
      next: (data) => {
        this.successMsg = "Article modifié avec succès";

        setTimeout(() => {
          this.successMsg = '';
        }, 3000);

        this.notification.success('Article modifié avec succès', 'Succès');
        this.articleDto = { stock: 0 };
        this.initializeData();
        this.getAllArticles(this.currentPage);
      },
      error: (error) => {
        console.error('Erreur lors de la modification:', error);
        this.errorMsg = error?.error?.errors || ['Erreur lors de la modification de l\'article'];
        this.message = error?.error?.message || '';
        this.notification.error(this.message, 'Erreur');
      }
    });
  }

  supprimerArticle(cod: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cet article ?')) {
      this.articleService.supprimerArticle(cod).subscribe({
        next: () => {
          this.notification.success('Article supprimé avec succès', 'Succès');
          this.getAllArticles(this.currentPage);

          // Optionnel : mettre à jour refNum après suppression
          this.updateRefNum();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.errorMsg = error?.error?.message || '';
          this.notification.error('Erreur lors de la suppression', 'Erreur');
        }
      });
    }
  }

  cancel() {
    this.articleDto = { stock: 0 };
    this.resetMessages();
  }

  getArticle(code: number) {
    this.articleService.getArticleByCode(code)
      .subscribe({
        next: (data: ArticleDto) => {
          this.articleDto = data;
          console.log('Article chargé:', this.articleDto);

          window.scrollTo({
            top: 0,
            left: 0,
            behavior: 'smooth'
          });
        },
        error: (error) => {
          this.message = 'Erreur lors de récupération de l\'article';
          console.error("Erreur lors du chargement de l'article:", error);
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }
      });
  }

  chercherArticles() {
    if (!this.keyword || this.keyword.trim() === '') {
      this.getAllArticles(this.currentPage);
      return;
    }

    this.articleService.chercherArticleByKeyword(this.keyword)
      .subscribe({
        next: (data) => {
          console.log("keyword", this.keyword);
          console.log("Data:", data);
          this.articles = data;
          this.resultNum = this.articles.length;
          console.log('Articles recherchés:', this.articles);
        },
        error: (error) => {
          this.articles = [];
          console.error("Erreur lors du chargement de l'article:", error);
        }
      });
  }
}
