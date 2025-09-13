import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ArticleService } from "../../app/services/article/article.service";

import * as MesArticlesActions from './mesArticles.actions';
import { catchError, map, mergeMap, of } from "rxjs";

@Injectable()
export class MesArticlesEffects{

  constructor(private actions$:Actions,private mesArticlesServices:ArticleService){}

  loadMesArticles$ = createEffect(() =>
    this.actions$.pipe(
      ofType(MesArticlesActions.loadMesArticles),
      mergeMap(() => this.mesArticlesServices.findAllMesArticles().pipe(
        map((list) => {
          console.log("Liste reçue de l'API :", list); // 🔍 Debug ici
          return MesArticlesActions.loadMesArticlesSuccess({ list });
        }),
        catchError((error) => of(MesArticlesActions.loadMesArticlesFailure({ error })))
      ))
    )
  );


}
