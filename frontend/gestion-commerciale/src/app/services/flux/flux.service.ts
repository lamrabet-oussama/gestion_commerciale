import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {FluxNormalControllerService, FluxNormalResponseDto} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class FluxService {

  constructor(private fluxService:FluxNormalControllerService) { }

  getArticlesFlux(articleId:number,year:number):Observable<FluxNormalResponseDto>{
    return this.fluxService.getFluxArticle(articleId,year);
  }
}
