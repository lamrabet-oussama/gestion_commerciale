import { Injectable } from '@angular/core';
import {AuthenticationRequest, AuthenticationService, RegisterRequest} from "../../api-client";
import {BehaviorSubject} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'token';
  private loggedIn$ = new BehaviorSubject<boolean>(this.hasValidToken());
  constructor(private authService:AuthenticationService,private router:Router) { }

  register(data:RegisterRequest){
    return this.authService.register(data);
  }
  authenticate(data:AuthenticationRequest){
    return this.authService.authenticate(data);
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    return token ? JSON.parse(token).token : null;
  }

  isLoggedIn(): boolean {
    return this.hasValidToken();
  }

  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // timestamp en ms
      return Date.now() < exp;
    } catch {
      return false;
    }
  }

  checkTokenValidity() {
    if (!this.hasValidToken()) {
      this.logout();
    }
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.loggedIn$.next(false);
    this.router.navigate(['/login']);
  }

  getLoggedInObservable() {
    return this.loggedIn$.asObservable();
  }

}
