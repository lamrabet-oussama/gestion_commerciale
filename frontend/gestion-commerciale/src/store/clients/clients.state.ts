import {TierDto, UserDto} from "src/app/api-client";

export interface ClientsState {
  list:TierDto[];
  error:any;
}

export const initialClientsList:ClientsState={
  list: [],
  error: null
}
