import { createAction, props } from "@ngrx/store";
import {TierDto} from "../../app/api-client";

export const loadClients=createAction('[Clients] Load All Clients');

export const loadClientsSuccess=createAction('[Clients] Load All Clients Success',props<{list:TierDto[]}>())

export const loadClientsFailure=createAction('[Clients] Load All Clients Failure',props<{error:any}>())
