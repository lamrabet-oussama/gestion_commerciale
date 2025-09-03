import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PageDashboardComponent} from "./pages/page-dashboard/page-dashboard.component";
import {PageArticleComponent} from "./pages/page-article/page-article.component";
import {PageTiersComponent} from "./pages/page-tiers/page-tiers.component";
import { EntrepriseProfileComponent } from './pages/entreprise-profile/entreprise-profile/entreprise-profile.component';
import { PageCaisseJourComponent } from './pages/page-caisse-jour/page-caisse-jour.component';
import {PageDettesComponent} from "./pages/page-dettes/page-dettes.component";
import {PageFluxComponent} from "./pages/page-flux/page-flux.component";
import {BonVenteComponent} from "./pages/bon-vente/bon-vente.component";
import {BonAchatComponent} from "./pages/bon-achat/bon-achat/bon-achat.component";
import {ReglementComponent} from "./pages/reglement/reglement.component";
import {TierSituationComponent} from "./pages/tier-situation/tier-situation.component";

const routes: Routes = [
  {
    path: '',
    component: PageDashboardComponent,
    children: [
      {
        path: 'articles',
        component: PageArticleComponent,
      },
      {
        path: 'tiers',
        component: PageTiersComponent,
      },
      {
        path: 'profile-entreprise',
        component: EntrepriseProfileComponent,
      },
      {
        path: 'page-caisse-jour',
        component:PageCaisseJourComponent
      },
      {
        path: 'dettes',
        component:PageDettesComponent
      },
      {
        path: 'flux',
        component:PageFluxComponent
      },
      {
        path:'bon-vente',
        component:BonVenteComponent
      },
      {
        path:'bon-achat',
        component:BonAchatComponent
      },
      {
        path:'reglement',
        component:ReglementComponent,
      },
      {
        path:'situation-tier',
        component:TierSituationComponent
      }
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
