import { createAction, props } from "@ngrx/store";
import {TierDto} from "../../app/api-client";

export const loadFournisseurs=createAction('[Fournisseurs] Load All Fournisseurs');

export const loadFournisseursSuccess=createAction('[Fournisseurs] Load All Fournisseurs Success',props<{list:TierDto[]}>())

export const loadFournisseursFailure=createAction('[Fournisseurs] Load All Fournisseurs Failure',props<{error:any}>())
