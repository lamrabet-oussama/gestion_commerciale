import { createReducer, on } from "@ngrx/store";
import { initialArticlesList } from "./mesArticles.state";
import * as MesArticlesActions from './mesArticles.actions';
export const mesArticlesReducers=createReducer(
  initialArticlesList,
  on(MesArticlesActions.loadMesArticles,(state)=>({
    ...state,
    error:null
  })),
  on(MesArticlesActions.loadMesArticlesSuccess,(state,{list})=>{
    return {
      ...state,
      list:list,
      error:null
    }
  }),
  on(MesArticlesActions.loadMesArticlesFailure,(state,{error})=>(
    {
      ...state,
      error:error
    }
  ))
)
