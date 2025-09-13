import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ArticleService } from "../../app/services/article/article.service";

import * as MesArticlesActions from './allUsers.actions';
import { catchError, map, mergeMap, of } from "rxjs";
import {UserControllerService} from "../../app/api-client";

@Injectable()
export class AllUsersEffects {

  constructor(private actions$:Actions,private userServices:UserControllerService){}

  loadMesArticles$ = createEffect(() =>
    this.actions$.pipe(
      ofType(MesArticlesActions.loadAllUsers),
      mergeMap(() => this.userServices.getAllUsers().pipe(
        map((list) => {
          console.log("Liste reçue de l'API :", list); // 🔍 Debug ici
          return MesArticlesActions.loadAllUsersSuccess({ list });
        }),
        catchError((error) => of(MesArticlesActions.loadAllUsersFailure({ error })))
      ))
    )
  );


}
