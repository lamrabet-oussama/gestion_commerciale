import {Component, OnInit} from '@angular/core';
import {ArticleService} from "../../services/article/article.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ArticleDto} from "../../api-client";
import {NotificationService} from "../../services/notification/notification.service";
import {ToastrService} from "ngx-toastr";
import {faTimes} from "@fortawesome/free-solid-svg-icons/faTimes";
import {faTags} from "@fortawesome/free-solid-svg-icons/faTags";
import {faFolderMinus} from "@fortawesome/free-solid-svg-icons/faFolderMinus";
import {faCheckCircle} from "@fortawesome/free-solid-svg-icons/faCheckCircle";
import {faPen} from "@fortawesome/free-solid-svg-icons/faPen";
import {faBox} from "@fortawesome/free-solid-svg-icons/faBox";
import {faDollarSign} from "@fortawesome/free-solid-svg-icons/faDollarSign";
import {faPercent} from "@fortawesome/free-solid-svg-icons/faPercent";
import {faStickyNote} from "@fortawesome/free-solid-svg-icons/faStickyNote";

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
  resultNum:number=0;

  articles: Array<ArticleDto> = [];
  faX = faTimes;
  refIcon=faTags;
  familleIcon=faFolderMinus;
  choixIcon=faCheckCircle;
  designationIcon=faPen;
  stockIcon=faBox;
  prixIcon=faDollarSign;
  tvaIcon=faPercent;
  noteIcon=faStickyNote;
  constructor(
    private router: Router,
    private articleService: ArticleService,
    private activatedRoute: ActivatedRoute,
    private notification: NotificationService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // Chargement des familles
    this.articleService.findAllFamilles()
      .subscribe({
        next: (data: string[]) => {
          this.familles = data;
          console.log('Familles chargées:', this.familles);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des familles:', error);
        }
      });

    // Chargement des choix
    this.articleService.findAllChoix()
      .subscribe({
        next: (data: string[]) => {
          this.choix = data;
          console.log('Choix chargés:', this.choix);
        },
        error: (error) => {
          console.error('Erreur lors du chargement des choix:', error);
        }
      });

    // Chargement des articles avec gestion d'erreur
    this.getAllArticles(this.currentPage);
    this.getTotal();
    this.articleDto.stock=0;
  }

  private resetMessages() {
    this.errorMsg = [];
    this.message = '';
    this.successMsg = '';
  }

  getTotal(){
    this.articleService.getTotalElements().subscribe({
      next:(value)=>{
        this.articleDto.ref=value+1;
      }
    })
  }
  getAllArticles(page: number): void {
    console.log(`Chargement des articles - Page: ${page}, PageSize: ${this.pageSize}`);

    this.articleService.getPaginatedArticles(page, this.pageSize).subscribe({
      next: (data) => {
        console.log('Réponse complète du service:', data);

        // Vérification de la structure de la réponse
        if (data && data.content) {
          this.articles = data.content;
          this.articleDto.ref=(data.content.length+2);
          this.resultNum=data.totalElements ?? 0;
          this.currentPage = data.currentPage ?? page;
          this.totalPages = data.totalPages ?? 0;

          console.log('Articles assignés:', this.articles);
          console.log('Page courante:', this.currentPage);
          console.log('Total pages:', this.totalPages);
          console.log('Nombre d\'articles:', this.articles.length);
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
gatArticle(code:number){

}
  validerArticle(): boolean {
    this.errorMsg = []; // Reset des erreurs avant validation
    let valide = true;


    if (!this.articleDto.ref || this.articleDto.ref <= 0) {
      this.errorMsg.push("La référence de l'article est obligatoire.");
      valide = false;
    }

    if (this.articleDto.stock!=null && this.articleDto.stock < 0) {
      this.errorMsg.push("Le stock de l'article est obligatoire.");
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
    this.errorMsg = []; // Reset des erreurs avant validation
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

    console.log("articleDto envoyé :", this.articleDto);

    this.articleService.creeArticle(this.articleDto).subscribe({
      next: (data) => {
        this.notification.success('Article ajouté avec succès', 'Succès');
        this.successMsg = 'Article ajouté avec succès';
        setTimeout(() => {
          this.successMsg = '';
        }, 3000);
        this.articleDto = {}; // Reset du formulaire
        this.getAllArticles(this.currentPage); // Recharger la liste
      },
      error: (error) => {
        this.notification.error(error?.error?.message)
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
        this.articleDto = {}; // Reset du formulaire
        this.getAllArticles(this.currentPage); // Recharger la liste
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
         /* this.successMsg="Article Supprimé"
          setTimeout(() => {
            this.successMsg = '';
          }, 3000);*/
          this.getAllArticles(this.currentPage);
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
    this.articleDto = {};
    this.resetMessages();
  }
  getArticle(code: number) {
    this.articleService.getArticleByCode(code)
      .subscribe({
        next: (data: ArticleDto) => {
          this.articleDto = data;
          console.log('Article chargé:', this.articleDto);

          window.scrollTo(0, 0);

          window.scrollTo({
            top: 0,
            left: 0,
            behavior: 'smooth'
          });
        },
        error: (error) => {
          this.message = 'Erreur lors de récupération de l\'article';
          console.error("Erreur lors du chargement de l'article:", error);

          // Scroll même en cas d'erreur pour voir le message
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
        console.log("kayword",this.keyword);
        console.log("Date:",data);
        this.articles = data;
        this.resultNum=this.articles.length;
        console.log('Articles recherchés:', this.articles);
      },
      error: (error) => {
        this.articles=[];

        console.error("Erreur lors du chargement de l\'article:", error);
      }
    });
}
}
