import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpRequest, HttpResponse} from "@angular/common/http";
import {Observable, tap} from "rxjs";
import {AuthenticationResponse} from "../../api-client";
import {LoaderService} from "../../composants/loader/loader.service";

@Injectable({
  providedIn: 'root'
})
export class InterceptorService {

  constructor(private loaderService: LoaderService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const excludedUrls = [
      '/gestioncommerciale/article',
      '/gestioncommerciale/tiers/client',
        '/gestioncommerciale/tiers/fournisseur',
      '/gestioncommerciale/tiers/',
      '/gestioncommerciale/tiers/all',
      'gestioncommerciale/bonsvente',

    ];

    // Vérifie si l'URL doit être exclue
    const isExcluded = excludedUrls.some(url => req.url.includes(url));

    if (!isExcluded) {
      this.loaderService.show(); // ✅ Affiche seulement si l'URL n'est pas exclue
    }

    const token = localStorage.getItem('token')
      ? JSON.parse(localStorage.getItem('token')!).token
      : null;

    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(authReq).pipe(
      tap({
        next: (event) => {
          if (!isExcluded && event instanceof HttpResponse) {
            this.loaderService.hide();
          }
        },
        error: (err) => {
          if (!isExcluded) {
            this.loaderService.hide();
          }
          if (err.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('currentUser');
            window.location.href = '/login';
          }
        }
      })
    );
  }

  private isAuthUrl(url: string): boolean {
    const authEndpoints = [
      '/gestioncommerciale/auth/authenticate',
      '/gestioncommerciale/auth/register'
    ];

    return authEndpoints.some(endpoint => url.includes(endpoint));
  }

  private addAuthHeader(req: HttpRequest<any>): HttpRequest<any> {
    const tokenStr = localStorage.getItem('token');

    if (tokenStr) {
      try {
        const authData: AuthenticationResponse = JSON.parse(tokenStr);
        if (authData.token) {
          return req.clone({
            setHeaders: { Authorization: `Bearer ${authData.token}` }
          });
        }
      } catch (e) {
        console.error('Token JSON invalide', e);
      }
    }

    return req;
  }
}
