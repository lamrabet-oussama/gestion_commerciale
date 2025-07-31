import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PageDashboardComponent} from "./pages/page-dashboard/page-dashboard.component";
import {PageArticleComponent} from "./pages/page-article/page-article.component";
import {PageTiersComponent} from "./pages/page-tiers/page-tiers.component";

const routes: Routes = [

  {
    path:'',
    component:PageDashboardComponent,
    children:[
      {
        path:'articles',
        component:PageArticleComponent
      },
      {
        path:'tiers',
        component:PageTiersComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
