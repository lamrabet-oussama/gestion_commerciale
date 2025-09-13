import { MesInfosState } from "src/store/mes-infos/mesInfos.state";
import {MesArticlesState} from "../store/mes-articles/mesArticles.state";
import {AllUsersState} from "../store/all-users/allUsers.state";
import {ClientsState} from "../store/clients/clients.state";
import {FournisseursState} from "../store/fournisseurs/fournisseurs.state";
import {CurrentUserState} from "../store/currentUser/currentUser.state";

export interface AppState{
    mesInfos:MesInfosState
    mesArticles:MesArticlesState
    allUsers:AllUsersState
    clients:ClientsState
    fournisseurs:FournisseursState
    currentUser:CurrentUserState
}
