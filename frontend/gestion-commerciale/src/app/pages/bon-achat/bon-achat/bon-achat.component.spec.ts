import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BonAchatComponent } from './bon-achat.component';

describe('BonAchatComponent', () => {
  let component: BonAchatComponent;
  let fixture: ComponentFixture<BonAchatComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BonAchatComponent]
    });
    fixture = TestBed.createComponent(BonAchatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
