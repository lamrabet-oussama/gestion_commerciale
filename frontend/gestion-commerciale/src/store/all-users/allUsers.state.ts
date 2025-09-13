import { UserDto} from "src/app/api-client";

export interface AllUsersState {
  list:UserDto[];
  error:any;
}

export const initialAllUsersList:AllUsersState={
  list: [],
  error: null
}
