import {Component, OnInit} from '@angular/core';
import { AppState } from 'src/app/app.state';
import { loadMesArticles } from 'src/store/mes-articles/mesArticles.actions';
import {Observable} from "rxjs";
import {ArticleDto, FluxNormalResponseDto} from "../../api-client";
import {Store} from "@ngrx/store";
import {selectArticles} from "../../../store/mes-articles/mesArticles.selectors";
import {NotificationService} from "../../services/notification/notification.service";
import {FluxService} from "../../services/flux/flux.service";
@Component({
  selector: 'app-page-flux',
  templateUrl: './page-flux.component.html',
  styleUrls: ['./page-flux.component.scss']
})
export class PageFluxComponent implements OnInit{

  selectedYear:number;
  maxYear:number;
  selectedArticleId:number=0;
  fluxesNormal:FluxNormalResponseDto={};

  mesArticles$: Observable<ArticleDto[] | []>;
    constructor(private store: Store<AppState>,private fluxServices:FluxService,private notificationService:NotificationService) {
    this.mesArticles$ = this.store.select(selectArticles);
      this.maxYear = new Date().getFullYear();
      this.selectedYear=new Date().getFullYear();
  }

  ngOnInit(): void {
    this.store.dispatch(loadMesArticles());

    this.mesArticles$.subscribe(articles => {
      console.log("Articles ALL:", articles);

      if (articles.length > 0 && !this.selectedArticleId) {
        this.selectedArticleId = articles[0].cod!;
        this.getFluxNormal();
      }
    });
  }

  getFluxNormal(): void {
    if (!this.selectedArticleId) {
      return;
    }
    this.fluxServices.getArticlesFlux(this.selectedArticleId, this.selectedYear).subscribe({
      next: (results) => {
        this.fluxesNormal = results;
        console.log(results)
      },
      error: (err) => {
        this.notificationService.error(err?.errors?.message || 'Erreur inconnue', 'Erreur');
      }
    });
  }

  onYearChange(year: number): void {
    this.selectedYear = year;
    this.getFluxNormal();
  }

  onArticleSelect(articleId: number): void {
    this.selectedArticleId = articleId;
    this.getFluxNormal();
  }

}



