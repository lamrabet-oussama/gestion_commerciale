import { Injectable } from '@angular/core';
import {ArticleDto, ArticlesService, PageResponse} from "../../api-client";
import {Observable, of} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {
  private apiUrl = 'http://localhost:8080/gestioncommerciale/articles'; // adapte à ton URL

  constructor(private articleService: ArticlesService,private http: HttpClient) { }
  getPaginatedArticles(page: number, size: number): Observable<PageResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse>(`${this.apiUrl}`, { params });
  }

  getTotalElements(){
    return this.articleService.getTotalElements();
  }

  supprimerArticle(cod:number){
    return this.articleService._delete(cod);
  }
  chercherArticleByKeyword(keyword:string){
    return this.articleService.searchArticles(keyword);
  }
  getArticleByCode(code:number){
    return this.articleService.findByCod(code);
  }
  creeArticle(article:ArticleDto):Observable<ArticleDto> {
    return this.articleService.save(article);
  }
  modifierArticle(code:number,article:ArticleDto):Observable<ArticleDto> {
    return this.articleService.update(code,article);
  }
  findAllArticles(page:number,size:number): Observable<PageResponse> {
    return this.articleService.getArticlesPaginated(page,size);
  }
  findArticleByCode(code?:number): Observable<ArticleDto> {
    if(code){
      return this.articleService.findByCod(code);

    }
    return of();
  }
  findAllFamilles():Observable<string[]>{
    return this.articleService.getFamilles();
  }
  findAllChoix():Observable<string[]>{
    return this.articleService.getChoix();
  }
}
