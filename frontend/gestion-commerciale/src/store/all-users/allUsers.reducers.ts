import { createReducer, on } from "@ngrx/store";
import { initialAllUsersList } from "./allUsers.state";
import * as AllUsersActions from './allUsers.actions';
export const allUsersReducers=createReducer(
  initialAllUsersList,
  on(AllUsersActions.loadAllUsers,(state)=>({
    ...state,
    error:null
  })),
  on(AllUsersActions.loadAllUsersSuccess,(state,{list})=>{
    return {
      ...state,
      list:list,
      error:null
    }
  }),
  on(AllUsersActions.loadAllUsersFailure,(state,{error})=>(
    {
      ...state,
      error:error
    }
  ))
)
