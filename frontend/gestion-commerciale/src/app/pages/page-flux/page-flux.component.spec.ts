import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageFluxComponent } from './page-flux.component';

describe('PageFluxComponent', () => {
  let component: PageFluxComponent;
  let fixture: ComponentFixture<PageFluxComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageFluxComponent]
    });
    fixture = TestBed.createComponent(PageFluxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
