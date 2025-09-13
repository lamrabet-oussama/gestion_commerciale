import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ArticleService } from "../../app/services/article/article.service";

import * as FournisseursActions from './fournisseurs.actions';
import { catchError, map, mergeMap, of } from "rxjs";
import {TiersService, UserControllerService} from "../../app/api-client";

@Injectable()
export class FournisseursEffects {

  constructor(private actions$:Actions,private fournisseurServices:TiersService){}

  loadFournisseurs$ = createEffect(() =>
    this.actions$.pipe(
      ofType(FournisseursActions.loadFournisseurs),
      mergeMap(() => this.fournisseurServices.getAllFournisseur().pipe(
        map((list) => {
          console.log("Liste Fournisseurs reçue de l'API :", list); // 🔍 Debug ici
          return FournisseursActions.loadFournisseursSuccess({ list });
        }),
        catchError((error) => of(FournisseursActions.loadFournisseursFailure({ error })))
      ))
    )
  );


}
