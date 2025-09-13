import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ArticleService } from "../../app/services/article/article.service";

import * as ClientsActions from './clients.actions';
import { catchError, map, mergeMap, of } from "rxjs";
import {TiersService, UserControllerService} from "../../app/api-client";

@Injectable()
export class ClientsEffects {

  constructor(private actions$:Actions,private clientServices:TiersService){}

  loadClients$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ClientsActions.loadClients),
      mergeMap(() => this.clientServices.getAllClient().pipe(
        map((list) => {
          console.log("Liste Clients reçue de l'API :", list); // 🔍 Debug ici
          return ClientsActions.loadClientsSuccess({ list });
        }),
        catchError((error) => of(ClientsActions.loadClientsFailure({ error })))
      ))
    )
  );


}
