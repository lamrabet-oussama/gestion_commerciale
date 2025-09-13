import { Injectable } from '@angular/core';
import {UpdateUserRequest, UserControllerService} from "../../api-client";
import {NotificationService} from "../notification/notification.service";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private userService:UserControllerService) { }

  getCurrentUser(){
    return this.userService.getCurrentUser();
  }

  updateUser(userId:number,request:UpdateUserRequest){
    return this.userService.updateUser(userId,request);

}

bloquerUser( userId:number){
    return this.userService.bloquerUser(userId);
}
debloquerUser(userId:number){
    return this.userService.debloquerUser(userId);
}
}
