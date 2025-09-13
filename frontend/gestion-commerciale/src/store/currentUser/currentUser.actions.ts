import { createAction, props } from "@ngrx/store";
import {ArticleDto, UserDto} from "src/app/api-client";

export const loadCurrentUser=createAction('[currentUser] Load Current User');

export const loadCurrentUserSuccess=createAction('[currentUser] Load Current User Success',props<{currentUser:UserDto|null}>())

export const loadCurrentUserFailure=createAction('[currentUser] Load Current User Failure',props<{error:any}>())
export const logoutCurrentUser=createAction('[currentUser] Logout Current User ')
