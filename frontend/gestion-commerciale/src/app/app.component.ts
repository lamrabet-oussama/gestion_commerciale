import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "./api-client";
import {AuthService} from "./services/auth/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'gestion-commerciale';
  currentYear = new Date().getFullYear();

  constructor(private authService: AuthService, private router:Router) { }
  ngOnInit() {
    // Vérifie le token au démarrage
    this.authService.checkTokenValidity();

    // Vérifie le token régulièrement (optionnel)
    setInterval(() => {
      this.authService.checkTokenValidity();
    }, 1000 * 5); // toutes les 5 secondes
  }
}
