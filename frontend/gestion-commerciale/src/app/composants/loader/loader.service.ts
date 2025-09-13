import { Injectable } from '@angular/core';
import {Subject} from "rxjs";
import {LoaderState} from "./loader.model";

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  constructor() { }

  private loaderSubject = new Subject<LoaderState>();

  loaderState = this.loaderSubject.asObservable();


  show(): void {
    this.loaderSubject.next({show: true});
  }

  hide(): void {
    this.loaderSubject.next({show: false});
  }
}
