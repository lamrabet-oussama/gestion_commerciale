import {Component, OnInit} from '@angular/core';
import {Menu} from "./menu";
import {Router} from "@angular/router";
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { MesInfoxDto } from 'src/app/api-client';
import { AppState } from 'src/app/app.state';
import { loadMesInfos } from 'src/store/mes-infos/mesInfos.actions';
import { selectInfos } from 'src/store/mes-infos/mesInfos.selectors';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent implements OnInit{
  public menuProperties: Array<Menu> = [
    {
      id: '1',
      title: 'Tableau de bord',
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
  ];
  mesInfos$: Observable<MesInfoxDto | null>;

  hover = false;

  ngOnInit(): void {
    this.store.dispatch(loadMesInfos());
  }
  menuOpen:boolean=false;
  constructor(private router: Router,private store: Store<AppState>) {
    this.mesInfos$ = this.store.select(selectInfos);

  }
  navigate(route: string) {
    this.router.navigate([route]);
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
