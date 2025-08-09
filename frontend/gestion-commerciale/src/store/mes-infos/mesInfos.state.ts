import { MesInfoxDto } from "src/app/api-client";

export interface MesInfosState{
    infos:MesInfoxDto|null;
    error:any;
}

export const initialMesInfosState:MesInfosState={
    infos: null,
    error: null
}