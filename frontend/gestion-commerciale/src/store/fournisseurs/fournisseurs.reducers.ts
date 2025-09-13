import { createReducer, on } from "@ngrx/store";
import { initialFournisseursList } from "./fournisseurs.state";
import * as ClientsActions from './fournisseurs.actions';
export const fournisseursReducers=createReducer(
  initialFournisseursList,
  on(ClientsActions.loadFournisseurs,(state)=>({
    ...state,
    error:null
  })),
  on(ClientsActions.loadFournisseursSuccess,(state,{list})=>{
    return {
      ...state,
      list:list,
      error:null
    }
  }),
  on(ClientsActions.loadFournisseursFailure,(state,{error})=>(
    {
      ...state,
      error:error
    }
  ))
)
