import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageCaisseJourComponent } from './page-caisse-jour.component';

describe('PageCaisseJourComponent', () => {
  let component: PageCaisseJourComponent;
  let fixture: ComponentFixture<PageCaisseJourComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageCaisseJourComponent]
    });
    fixture = TestBed.createComponent(PageCaisseJourComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
