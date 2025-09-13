import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from "@angular/common/http";
import { PageDashboardComponent } from './pages/page-dashboard/page-dashboard.component';
import { PageArticleComponent } from './pages/page-article/page-article.component';
import {MenuComponent} from "./composants/menu/menu.component";
import {PageTiersComponent} from "./pages/page-tiers/page-tiers.component";
import { HeaderComponent } from './composants/header/header.component';
import { DetailArticleComponent } from './composants/detail-article/detail-article.component';
import { PaginationComponent } from './composants/pagination/pagination.component';
import {ApiModule, Configuration} from "./api-client";
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import { EntrepriseProfileComponent } from './pages/entreprise-profile/entreprise-profile/entreprise-profile.component';
import { PageCaisseJourComponent } from './pages/page-caisse-jour/page-caisse-jour.component';
import { StoreModule } from '@ngrx/store';
import { mesInfosReducer } from 'src/store/mes-infos/mesInfos.reducers';
import { EffectsModule } from '@ngrx/effects';
import { MesInfosEffects } from 'src/store/mes-infos/mesInfos.effects';
import { PageDettesComponent } from './pages/page-dettes/page-dettes.component';
import { PageFluxComponent } from './pages/page-flux/page-flux.component';
import {mesArticlesReducers} from "../store/mes-articles/mesArticles.reducers";
import {MesArticlesEffects} from "../store/mes-articles/mesArticles.effects";
import { BonVenteComponent } from './pages/bon-vente/bon-vente.component';
import { BonAchatComponent } from './pages/bon-achat/bon-achat/bon-achat.component';
import {allUsersReducers} from "../store/all-users/allUsers.reducers";
import {AllUsersEffects} from "../store/all-users/allUsers.effects";
import {ClientsEffects} from "../store/clients/clients.effects";
import {clientsReducers} from "../store/clients/clients.reducers";
import {fournisseursReducers} from "../store/fournisseurs/fournisseurs.reducers";
import {FournisseursEffects} from "../store/fournisseurs/fournisseurs.effects";
import { ReglementComponent } from './pages/reglement/reglement.component';
import { TierSituationComponent } from './pages/tier-situation/tier-situation.component';
import { PageLoginComponent } from './pages/page-login/page-login.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoaderComponent } from './composants/loader/loader.component';
import {InterceptorService} from './services/interceptor/interceptor.service';
import {CurrentUserEffects} from "../store/currentUser/currentUser.effects";
import {currentUserReducers} from "../store/currentUser/currentUser.reducers";
import {StoreDevtoolsModule} from "@ngrx/store-devtools";
import { ModifierUtilisateurComponent } from './pages/modifier-utilisateur/modifier-utilisateur.component';
import { NousComponent } from './pages/nous/nous.component';


@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    PageDashboardComponent,
    PageArticleComponent,
    PageTiersComponent,
    HeaderComponent,
    DetailArticleComponent,
    PaginationComponent,
    EntrepriseProfileComponent,
    PageCaisseJourComponent,
    PageDettesComponent,
    PageFluxComponent,
    BonVenteComponent,
    BonAchatComponent,
    ReglementComponent,
    TierSituationComponent,
    PageLoginComponent,
    RegisterComponent,
    LoaderComponent,
    ModifierUtilisateurComponent,
    NousComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    FontAwesomeModule,
    StoreDevtoolsModule.instrument({
      maxAge: 25, // nombre d'états à conserver
    }),
    StoreModule.forRoot({mesInfos:mesInfosReducer,mesArticles:mesArticlesReducers,allUsers:allUsersReducers,clients:clientsReducers,fournisseurs:fournisseursReducers,currentUser:currentUserReducers}),
    EffectsModule.forRoot([MesInfosEffects,MesArticlesEffects,AllUsersEffects,ClientsEffects,FournisseursEffects,CurrentUserEffects]),
    ApiModule.forRoot(() => new Configuration({
      basePath: 'http://localhost:8080'
    })),
    BrowserAnimationsModule,
    ToastrModule.forRoot(
      {
        positionClass: 'toast-center',
      }
    ),
    ReactiveFormsModule
  ],
  providers: [HttpClient,{
    provide:HTTP_INTERCEPTORS,
    useClass:InterceptorService,
    multi: true
  },
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
