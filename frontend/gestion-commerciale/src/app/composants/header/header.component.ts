import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { MesInfoxDto } from 'src/app/api-client';
import { AppState } from 'src/app/app.state';
import { loadMesInfos } from 'src/store/mes-infos/mesInfos.actions';
import { selectInfos } from 'src/store/mes-infos/mesInfos.selectors';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  mesInfos$: Observable<MesInfoxDto | null>;
  constructor(private store: Store<AppState>) {
    this.mesInfos$ = this.store.select(selectInfos);
  }
  hover = false;

  ngOnInit(): void {
    this.store.dispatch(loadMesInfos());
  }
}
