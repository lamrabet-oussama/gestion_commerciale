import { Component } from '@angular/core';
import {Menu} from "./menu";
import {Router} from "@angular/router";


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent {
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
  ];

  menuOpen:boolean=false;
  constructor(private router: Router) {}
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
