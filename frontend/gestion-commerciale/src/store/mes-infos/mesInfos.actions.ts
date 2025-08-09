import { createAction, props } from "@ngrx/store";
import { MesInfoxDto } from "src/app/api-client";

export const loadMesInfos=createAction('[Mes Infos] Load Mes Infos');

export const loadMesInfosSuccess=createAction('[MesInfos] Load Infos Success',props<{infos:MesInfoxDto}>())

export const loadMesInfosFailure=createAction('[Mes Infos] Load Infos Failure',props<{error:any}>())