import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClient, HttpClientModule} from "@angular/common/http";
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

@NgModule({
  declarations: [
    AppComponent,
    MenuComponent,
    PageDashboardComponent,
    PageArticleComponent,
    PageTiersComponent,
    HeaderComponent,
    DetailArticleComponent,
    PaginationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    FontAwesomeModule,
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
  providers: [HttpClient],
  bootstrap: [AppComponent]
})
export class AppModule { }
