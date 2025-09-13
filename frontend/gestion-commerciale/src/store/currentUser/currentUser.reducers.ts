import { createReducer, on } from "@ngrx/store";
import { initialCurrentUserState } from "./currentUser.state";
import * as CurrentUserActions from './currentUser.actions';
export const currentUserReducers=createReducer(
  initialCurrentUserState,
  on(CurrentUserActions.loadCurrentUser,(state)=>({
    ...state,
    error:null
  })),
  on(CurrentUserActions.loadCurrentUserSuccess,(state,{currentUser})=>{
    return {
      ...state,
      currentUser:currentUser,
      error:null
    }
  }),
  on(CurrentUserActions.loadCurrentUserFailure,(state,{error})=>(
    {
      ...state,
      error:error
    }
  )),
on(CurrentUserActions.logoutCurrentUser, state => ({
  ...state,
  currentUser: null
}))

)
