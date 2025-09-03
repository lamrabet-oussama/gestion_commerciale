import { Injectable } from '@angular/core';
import {ArticleDto, ArticlesService, PageResponse} from "../../api-client";
import {Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ArticleService {

  constructor(private articleService: ArticlesService) { }
  getPaginatedArticles(page: number, size: number): Observable<PageResponse> {


    return this.articleService.getArticlesPaginated(page, size);
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

  findAllMesArticles(){
    return this.articleService.findAll();
  }

  findAllFamilles():Observable<string[]>{
    return this.articleService.getFamilles();
  }
  findAllChoix():Observable<string[]>{
    return this.articleService.getChoix();
  }

  findArticleByDesignationAndChoix(designation:string,choix:string){
    return this.articleService.getArticleByDesAndChoix(designation,choix);
  }
  getAllDesognations(){
    return this.articleService.getAllDesignation();
  }

  getChoixByDes(des:string){
    return this.articleService.getChoixByDes(des);
  }
}
