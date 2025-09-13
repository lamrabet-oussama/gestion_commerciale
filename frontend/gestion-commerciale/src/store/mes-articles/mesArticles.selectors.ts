import { createFeatureSelector, createSelector } from "@ngrx/store";
import { MesArticlesState } from "./mesArticles.state";
import { state } from "@angular/animations";

export const selectMesArticlesState=createFeatureSelector<MesArticlesState>('mesArticles');

export const selectArticles=createSelector(
  selectMesArticlesState,
  (state)=>state.list
)

export const selectError=createSelector(
  selectMesArticlesState,
  (state)=>state.error
)
