import { createFeatureSelector, createSelector } from "@ngrx/store";
import { MesInfosState } from "./mesInfos.state";
import { state } from "@angular/animations";

export const selectMesInfosState=createFeatureSelector<MesInfosState>('mesInfos');

export const selectInfos=createSelector(
    selectMesInfosState,
    (state)=>state.infos
)

export const selectError=createSelector(
    selectMesInfosState,
    (state)=>state.error
)