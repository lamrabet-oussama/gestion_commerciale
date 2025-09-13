import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {Observable, map, filter, catchError, of, switchMap, take} from 'rxjs';
import { selectCurrentUser } from '../../../store/currentUser/currentUser.selectors';
import { NotificationService } from '../notification/notification.service';
import {loadCurrentUser} from "../../../store/currentUser/currentUser.actions";

export type AccessType = 'adminOnly' | 'activeUser' | 'anyAuthenticated'|'nonAuth';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private store: Store,
    private router: Router,
    private notificationService: NotificationService
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const accessType = route.data['accessType'] as AccessType;

    // ✅ Vérifier d'abord si il y a un token et charger l'utilisateur
    return this.checkAndLoadUser().pipe(
      switchMap(() => this.store.select(selectCurrentUser)),
      filter(user => user !== undefined), // Attendre que le user soit défini
      take(1), // Prendre seulement la première valeur valide
      map(user => {
        switch (accessType) {
          case 'nonAuth':
            if (!user) return true; // Pas d'utilisateur connecté → accès autorisé
            this.router.navigate(['/']); // Redirige vers la page d'accueil
            return false;

          case 'activeUser':
            if (user && user.etat === true) return true;
            this.notificationService.error('Votre compte est bloqué', 'Erreur');
            this.router.navigate(['/login']);
            return false;

          case 'adminOnly':
            if (user && user.role === 'ADMIN' && user.etat === true) return true;
            this.notificationService.error('Accès réservé aux administrateurs', 'Erreur');
            this.router.navigate(['/login']);
            return false;

          default:
            return false;
        }
      })
      ,
      catchError(() => {
        this.router.navigate(['/login']);
        return of(false);
      })
    );
  }

  private checkAndLoadUser(): Observable<any> {
    return this.store.select(selectCurrentUser).pipe(
      take(1),
      switchMap(user => {
        // Si pas d'utilisateur, essayer de charger depuis le token
        if (!user) {
          const token = localStorage.getItem('token');
          if (token) {
            try {
              const authData = JSON.parse(token);
              // Dispatcher l'action pour charger l'utilisateur
              this.store.dispatch(loadCurrentUser());
              // Attendre que l'utilisateur soit chargé
              return this.store.select(selectCurrentUser).pipe(
                filter(loadedUser => loadedUser !== undefined),
                take(1)
              );
            } catch (e) {
              return of(null);
            }
          }
          return of(null);
        }
        return of(user);
      })
    );
  }
}
