import {TierDto, UserDto} from "src/app/api-client";

export interface FournisseursState {
  list:TierDto[];
  error:any;
}

export const initialFournisseursList:FournisseursState={
  list: [],
  error: null
}
