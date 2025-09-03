import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TierSituationComponent } from './tier-situation.component';

describe('TierSituationComponent', () => {
  let component: TierSituationComponent;
  let fixture: ComponentFixture<TierSituationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TierSituationComponent]
    });
    fixture = TestBed.createComponent(TierSituationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
