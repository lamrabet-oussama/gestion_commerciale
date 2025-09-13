import { createFeatureSelector, createSelector } from "@ngrx/store";
import { CurrentUserState } from "./currentUser.state";
import { state } from "@angular/animations";

export const selectCurrentUserState=createFeatureSelector<CurrentUserState>('currentUser');

export const selectCurrentUser=createSelector(
  selectCurrentUserState,
  (state)=>state.currentUser
)

export const selectError=createSelector(
  selectCurrentUserState,
  (state)=>state.error
)
