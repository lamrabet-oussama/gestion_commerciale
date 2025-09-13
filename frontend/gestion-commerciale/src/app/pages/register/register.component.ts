import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../../services/notification/notification.service";
import {RegisterRequest} from "../../api-client";
import RoleEnum = RegisterRequest.RoleEnum;

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  roles = Object.values(RoleEnum); // ['ADMIN', 'USER', 'MANAGER']
  registerForm: FormGroup;
  constructor(private fb: FormBuilder,
              private authService: AuthService,private router: Router,private notificationService: NotificationService) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      gsm: ['', Validators.required],
      password: ['', Validators.required],
      role: ['', Validators.required]
    });
  }


  onSubmit() {
    if (this.registerForm.valid) {
      this.authService.register(this.registerForm.value).subscribe({
        next: () => {
          this.notificationService.success("Utilisateur créé",'Succès');
        },
        error: (err) => {
          this.notificationService.error(err?.error?.message || err?.erros,'Erreur');
          this.router.navigate(['/inscrire']);

          console.error('Erreur lors de l\'inscriprion', err);
        },
      });
    }
  }

}
