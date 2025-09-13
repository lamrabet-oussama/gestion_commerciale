import {Component, OnInit} from '@angular/core';
import {RegisterRequest, UpdateUserRequest, UserDto} from "../../api-client";
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {UserService} from "../../services/user/user.service";
import {Store} from "@ngrx/store";
import {AppState} from "../../app.state";
import {selectCurrentUser, selectCurrentUserState} from "../../../store/currentUser/currentUser.selectors";
import {selectUsers} from "../../../store/all-users/allUsers.selectors";
import {NotificationService} from "../../services/notification/notification.service";
import {loadAllUsers} from "../../../store/all-users/allUsers.actions";
import {loadCurrentUser} from "../../../store/currentUser/currentUser.actions";
import RoleEnum = RegisterRequest.RoleEnum;
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-modifier-utilisateur',
  templateUrl: './modifier-utilisateur.component.html',
  styleUrls: ['./modifier-utilisateur.component.scss']
})
export class ModifierUtilisateurComponent implements OnInit{
  roles = Object.values(RoleEnum); // ['ADMIN', 'USER', 'MANAGER']

  allUsersDto:UserDto[]|[]=[];
  allUsers$:Observable<UserDto[]|[]>;
  currentUser$:Observable<UserDto|null>;
  selectedUserId:number=-1;
  newuser:UpdateUserRequest={} as UpdateUserRequest;
  selectedUser:UserDto|null=null;
  user:UserDto|null=null;
  formVals:FormGroup;
  constructor(private userService: UserService, private fb: FormBuilder,private router: Router,private store:Store<AppState>,private notificationService: NotificationService) {
    this.store.dispatch(loadAllUsers());
    this.store.dispatch(loadCurrentUser());
    this.allUsers$ = this.store.select(selectUsers);
    this.currentUser$=this.store.select(selectCurrentUser);
    this.allUsers$.subscribe(
      {
        next: (users: UserDto[]) => {
          this.allUsersDto=users;
          console.log("All Users:",this.allUsersDto);
        },
        error: (error) => {
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      }
    )
    this.formVals = this.fb.group({
      login: ['', Validators.required],
      gsm: ['', Validators.required],
      password: [''], // Rendu facultatif
      role: ['', Validators.required]
    });
  }
  ngOnInit() {

  }

  onSubmit() {
    if (this.formVals.valid) {
    this.updateUser();
    }
  }
  onUserSelected(user: UserDto) {
    this.selectedUser = user; // Conversion en number

    this.selectedUserId=user.cod ?? -1;
    if (this.selectedUser) {
      this.formVals.patchValue({
        login: this.selectedUser.username,
        gsm: this.selectedUser.gsm,
        role: this.selectedUser.role
      });
    }
  }

  updateUser(){
    if(this.selectedUserId===-1){
      this.notificationService.error("Aucune Modification à attribué",'Erreur');
      return;
    }
    console.log("newUser:",this.formVals.value);
    console.log("UseId:",this.selectedUserId);

    this.userService.updateUser(this.selectedUserId,this.formVals.value).subscribe(
      {
        next:(user)=>{
          this.notificationService.success("Utilisateur modifié",'Succès');
        },
        error: (error) => {
          console.log(error?.error);
          this.notificationService.error(error?.error?.message, 'Erreur');
        }
      }
    )
  }
  bloquerUser(){
    this.userService.bloquerUser(this.selectedUserId).subscribe(
      {
        next:(user)=>{
          this.notificationService.success("Utilisateur bloqué",'Succès');
        },
        error: (error) => {
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      }
    )
  }

  debloquerUser(){
    this.userService.debloquerUser(this.selectedUserId).subscribe(
      {
        next:(user)=>{
          this.notificationService.success("Utilisateur débloqué",'Succès');
        },
        error: (error) => {
          this.notificationService.error(error?.error?.message,'Erreur');
        }
      }
    )
  }

}
