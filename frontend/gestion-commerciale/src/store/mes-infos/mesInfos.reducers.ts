import { createReducer, on } from "@ngrx/store";
import { initialMesInfosState } from "./mesInfos.state";
import * as MesInfosActions from './mesInfos.actions';
export const mesInfosReducer=createReducer(
    initialMesInfosState,
    on(MesInfosActions.loadMesInfos,(state)=>({
        ...state,
        error:null
    })),
    on(MesInfosActions.loadMesInfosSuccess,(state,{infos})=>{
        return {
            ...state,
            infos:infos,
            error:null
        }
    }),
    on(MesInfosActions.loadMesInfosFailure,(state,{error})=>(
        {
            ...state,
            error:error
        }
    ))
)