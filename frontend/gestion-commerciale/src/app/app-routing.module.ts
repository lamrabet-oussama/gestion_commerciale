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
import {PageLoginComponent} from "./pages/page-login/page-login.component";
import {RegisterComponent} from "./pages/register/register.component";
import {AuthGuard} from "./services/guard/guard.service";
import {ModifierUtilisateurComponent} from "./pages/modifier-utilisateur/modifier-utilisateur.component";
import {NousComponent} from "./pages/nous/nous.component";

const routes: Routes = [

  {
    path:'login',
    component:PageLoginComponent,
    canActivate: [AuthGuard],
    data: { accessType: 'nonAuth' }

  },
  {

    path: '',
    component: PageDashboardComponent,
    children: [
      {
        path: '',
        component: NousComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }

      },
      {
        path: 'articles',
        component: PageArticleComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }

      },
      {
        path: 'tiers',
        component: PageTiersComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path: 'profile-entreprise',
        component: EntrepriseProfileComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'adminOnly' }
      },
      {
        path: 'page-caisse-jour',
        component:PageCaisseJourComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'adminOnly' }
      },
      {
        path: 'dettes',
        component:PageDettesComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path: 'flux',
        component:PageFluxComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path:'bon-vente',
        component:BonVenteComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path:'bon-achat',
        component:BonAchatComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path:'reglement',
        component:ReglementComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path:'situation-tier',
        component:TierSituationComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'activeUser' }
      },
      {
        path:'inscrire',
        component:RegisterComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'adminOnly' }
      },
      {
        path:'update-user',
        component:ModifierUtilisateurComponent,
        canActivate: [AuthGuard],
        data: { accessType: 'adminOnly' }
      },

    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
