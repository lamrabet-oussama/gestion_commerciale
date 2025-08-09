import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { EntrepriseProfileService } from "src/app/services/entreprise-profile/entreprise-profile.service";

import * as MesInfosActions from './mesInfos.actions';
import { catchError, map, mergeMap, of } from "rxjs";

@Injectable()
export class MesInfosEffects{

    constructor(private actions$:Actions,private mesInfosService:EntrepriseProfileService){}

    loadMesInfos$ = createEffect(() =>
        this.actions$.pipe(
            ofType(MesInfosActions.loadMesInfos),
            mergeMap(() => this.mesInfosService.getInfos().pipe(
                map((infos) => MesInfosActions.loadMesInfosSuccess({ infos })),
                catchError((error) => of(MesInfosActions.loadMesInfosFailure({ error })))
            ))
        )
    );
}