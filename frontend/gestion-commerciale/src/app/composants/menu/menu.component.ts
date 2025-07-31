import { Component } from '@angular/core';
import {Menu} from "./menu";
import {Router} from "@angular/router";


@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent {

  public menuProperties:Array<Menu> = [
    {
      id:'1',
      title:'Tableau de bord',
      icon:'fa-solid fa-boxes-stacked',
      url:'/'
    },
    {
    id:'2',
    title:'Articles',
    icon:'fa-solid fa-boxes-stacked',
    url:'/articles'
  },
    {
      id:'3',
      title:'Fournisseurs / Clients',
      icon:'',
      url:'/tiers'
    },

  ]

  constructor(private router:Router) {
  }
  navigate(route:string){
    this.router.navigate([route]);
  }

}
