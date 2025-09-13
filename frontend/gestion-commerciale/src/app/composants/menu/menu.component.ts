import {Component, OnInit} from '@angular/core';
import {Menu} from "./menu";
import {Router} from "@angular/router";
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {MesInfoxDto, UserDto} from 'src/app/api-client';
import { AppState } from 'src/app/app.state';
import { loadMesInfos } from 'src/store/mes-infos/mesInfos.actions';
import { selectInfos } from 'src/store/mes-infos/mesInfos.selectors';
import {CurrentUserState} from "../../../store/currentUser/currentUser.state";
import {selectCurrentUserState} from "../../../store/currentUser/currentUser.selectors";
import {LogoutService} from "../../services/logout/logout.service";
import {faCaretRight} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent implements OnInit{
  currentUser$: Observable<CurrentUserState | null>;
  user:UserDto|null=null;
  public menuProperties: Array<Menu> = [
    {
      id: '1',
      title: 'Qui sommes-nous ?',
      icon: 'fa-solid fa-boxes-stacked',
      url: '/',
    },
    {
      id: '2',
      title: 'Articles',
      icon: 'fa-solid fa-boxes-stacked',
      url: '/articles',
    },
    {
      id: '3',
      title: 'Frs & Clients',
      icon: '',
      url: '/tiers',
    },
    {
      id: '4',
      title: 'Caisse Jounalière',
      icon: '',
      url: '/page-caisse-jour',
    },
    {
      id: '5',
      title: 'Entreprise Profile',
      icon: '',
      url: '/profile-entreprise',
    },
    {
      id: '6',
      title: 'Dettes & Créances',
      icon: '',
      url: '/dettes',
    },
    {
      id: '7',
      title: 'Flux',
      icon: '',
      url: '/flux',
    },
    {
      id: '8',
      title: 'Bon Vente',
      icon: '',
      url: '/bon-vente',
    },
    {
      id: '9',
      title: 'Bon Achat',
      icon: '',
      url: '/bon-achat',
    },
    {
      id: '10',
      title: 'Réglement de Crédit',
      icon: '',
      url: '/reglement',
    },
    {
      id: '11',
      title: 'Situation Tier',
      icon: '',
      url: '/situation-tier',
    },
    {
      id: '12',
      title: 'Ajouter Utilisateur',
      icon: '',
      url: '/inscrire',
    },
    {
      id: '13',
      title: 'Modifier Utilisateur',
      icon: '',
      url: '/update-user',
    },
  ];
  filteredMenuProperties: Menu[] = [];

  mesInfos$: Observable<MesInfoxDto | null>;

  hover = false;
   adminOnlyUrls = ['/inscrire', '/page-caisse-jour', '/profile-entreprise','/update-user'];

    faLogout=faCaretRight;
  ngOnInit(): void {
    this.store.dispatch(loadMesInfos());

    this.currentUser$.subscribe(userState => {
      if (userState?.currentUser) {
         this.user = userState.currentUser;
        const isUserActive = this.user.etat === true;
        const isAdmin = this.user.role === 'ADMIN';

        this.filteredMenuProperties = this.menuProperties.filter(menu => {
          // Si utilisateur inactif, cacher tout sauf dashboard
          if (!isUserActive && menu.url !== '/') return false;

          // Pages réservées aux admins
          if (this.adminOnlyUrls.includes(menu.url) && !isAdmin) return false;

          return true;
        });
      } else {
        // Aucun utilisateur connecté
        this.filteredMenuProperties = [];
      }
    });
  }  menuOpen:boolean=false;
  constructor(private router: Router,private store: Store<AppState>,private logoutSrv:LogoutService) {
    this.mesInfos$ = this.store.select(selectInfos);
    this.currentUser$ = this.store.select(selectCurrentUserState);


  }
  navigate(route: string) {
    this.router.navigate([route]);


  }
  logout(){
    this.logoutSrv.logout();
  }
  toggleMenu() {
    const layoutMenu = document.getElementById('layout-menu');
    if (layoutMenu) {
          this.menuOpen = true;

      layoutMenu.classList.toggle('open');
    }
  }
  closeMenu() {
    const layoutMenu = document.getElementById('layout-menu');
    if (layoutMenu) {

    this.menuOpen = false;
      layoutMenu.classList.remove('open');
    }
  }


}
