import { Injectable } from '@angular/core';
import { UserControllerService } from 'src/app/api-client';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  constructor(private userService:UserControllerService) { }
  getAllUsers() {
    return this.userService.getAllUsers();
  }
}
