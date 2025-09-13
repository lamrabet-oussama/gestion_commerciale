import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { ArticleService } from "../../app/services/article/article.service";

import * as CurrentUser from './currentUser.actions';
import { catchError, map, mergeMap, of } from "rxjs";
import {UserService} from "../../app/services/user/user.service";

@Injectable()
export class CurrentUserEffects {

  constructor(private actions$:Actions,private userService:UserService){}

  loadCurrentUser$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CurrentUser.loadCurrentUser),
      mergeMap(() =>
        this.userService.getCurrentUser().pipe(
          map((currentUser) => {
            console.log('[Effect] Réponse API currentUser:', currentUser);

            // ✅ Sauvegarde simple dans localStorage
            localStorage.setItem('currentUser', JSON.stringify(currentUser));

            return CurrentUser.loadCurrentUserSuccess({ currentUser });
          }),
          catchError((error) => of(CurrentUser.loadCurrentUserFailure({ error })))
        )
      )
    )
  );


}
