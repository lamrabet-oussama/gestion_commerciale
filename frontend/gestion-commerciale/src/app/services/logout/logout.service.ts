import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {NotificationService} from "../notification/notification.service";
import {Store} from "@ngrx/store";
import {logoutCurrentUser} from "../../../store/currentUser/currentUser.actions";

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private router:Router,private notification:NotificationService,private store: Store,
  ) { }

public logout(){
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  this.store.dispatch(logoutCurrentUser());

  this.router.navigate(['/login']).then(() => {
    this.notification.success("Logout successfully", "Succès");
  });

}
}
