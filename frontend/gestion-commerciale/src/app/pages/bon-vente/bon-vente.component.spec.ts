import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BonVenteComponent } from './bon-vente.component';

describe('BonVenteComponent', () => {
  let component: BonVenteComponent;
  let fixture: ComponentFixture<BonVenteComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BonVenteComponent]
    });
    fixture = TestBed.createComponent(BonVenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
