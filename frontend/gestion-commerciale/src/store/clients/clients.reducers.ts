import { createReducer, on } from "@ngrx/store";
import { initialClientsList } from "./clients.state";
import * as ClientsActions from './clients.actions';
export const clientsReducers=createReducer(
  initialClientsList,
  on(ClientsActions.loadClients,(state)=>({
    ...state,
    error:null
  })),
  on(ClientsActions.loadClientsSuccess,(state,{list})=>{
    return {
      ...state,
      list:list,
      error:null
    }
  }),
  on(ClientsActions.loadClientsFailure,(state,{error})=>(
    {
      ...state,
      error:error
    }
  ))
)
