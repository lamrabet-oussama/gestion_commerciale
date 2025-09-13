import { createAction, props } from "@ngrx/store";
import {ArticleDto, UserDto} from "src/app/api-client";

export const loadAllUsers=createAction('[AllUsers] Load All Users');

export const loadAllUsersSuccess=createAction('[AllUsers] Load All Users Success',props<{list:UserDto[]|[]}>())

export const loadAllUsersFailure=createAction('[AllUsers] Load All Users Failure',props<{error:any}>())
