import { createFeatureSelector, createSelector } from "@ngrx/store";
import { AllUsersState } from "./allUsers.state";
import { state } from "@angular/animations";

export const selectAllUsersState=createFeatureSelector<AllUsersState>('allUsers');

export const selectUsers=createSelector(
  selectAllUsersState,
  (state)=>state.list
)

export const selectError=createSelector(
  selectAllUsersState,
  (state)=>state.error
)
