import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {AuthService} from "../../services/auth/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../../services/notification/notification.service";
import {Store} from "@ngrx/store";
import {AppState} from "../../app.state";
import {selectCurrentUser, selectCurrentUserState} from "../../../store/currentUser/currentUser.selectors";
import {Observable} from "rxjs";
import {CurrentUserState} from "../../../store/currentUser/currentUser.state";
import {loadCurrentUser} from "../../../store/currentUser/currentUser.actions";
import {UserDto} from "../../api-client";
import {loadAllUsers} from "../../../store/all-users/allUsers.actions";
import {selectUsers} from "../../../store/all-users/allUsers.selectors";

@Component({
  selector: 'app-page-login',
  templateUrl: './page-login.component.html',
  styleUrls: ['./page-login.component.scss']
})

export class PageLoginComponent {
  loginForm: FormGroup;
  currentUser$: Observable<CurrentUserState | null>;
  allUsersDto:UserDto[]|[]=[];
  allUsers$:Observable<UserDto[]|[]>;
  constructor(private fb: FormBuilder,
              private authService: AuthService,private router: Router,private notificationService: NotificationService,private store:Store<AppState>) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
    this.currentUser$ = this.store.select(selectCurrentUserState);


    this.store.dispatch(loadAllUsers());
    this.allUsers$ = this.store.select(selectUsers);
    this.allUsers$.subscribe(
      {
        next: (users: UserDto[]) => {
          this.allUsersDto=users;
          console.log("All Users:",this.allUsersDto);
        },
        error: (error) => {
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      })

  }


  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.authenticate(this.loginForm.value).subscribe({
        next: (response) => {
          localStorage.setItem('token', JSON.stringify(response));
          this.store.dispatch(loadCurrentUser());
          this.notificationService.success("Login successfully",'Succès');
          this.currentUser$.subscribe(user => {
            if (user && user.currentUser) {
              console.log('Utilisateur connecté:', user.currentUser?.username);
              this.router.navigate(['/']);

            }
          });
        },
        error: (err) => {
          this.notificationService.error(err?.error?.message || err?.erros,'Erreur');
          this.router.navigate(['/login']);

          console.error('Erreur lors de l\'authentification', err);
        },
      });
    }
  }
}
