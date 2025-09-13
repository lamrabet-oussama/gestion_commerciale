import { createFeatureSelector, createSelector } from "@ngrx/store";
import { FournisseursState} from "./fournisseurs.state";
import { state } from "@angular/animations";

export const selectFournisseursState=createFeatureSelector<FournisseursState>('fournisseurs');

export const selectFournisseursList=createSelector(
  selectFournisseursState,
  (state)=>state.list
)

export const selectClientsError=createSelector(
  selectFournisseursState,
  (state)=>state.error
)
