import { UserDto} from "src/app/api-client";

export interface CurrentUserState {
  currentUser:UserDto|null;
  error:any;
}

export const initialCurrentUserState:CurrentUserState={
  currentUser: JSON.parse(localStorage.getItem('currentUser') || 'null'),
  error: null
}
