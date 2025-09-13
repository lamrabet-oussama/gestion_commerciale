import { createFeatureSelector, createSelector } from "@ngrx/store";
import { ClientsState } from "./clients.state";
import { state } from "@angular/animations";

export const selectClientsState=createFeatureSelector<ClientsState>('clients');

export const selectClientsList=createSelector(
  selectClientsState,
  (state)=>state.list
)

export const selectClientsError=createSelector(
  selectClientsState,
  (state)=>state.error
)
