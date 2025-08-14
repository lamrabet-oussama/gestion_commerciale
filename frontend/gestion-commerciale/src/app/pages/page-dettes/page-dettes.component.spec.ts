import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageDettesComponent } from './page-dettes.component';

describe('PageDettesComponent', () => {
  let component: PageDettesComponent;
  let fixture: ComponentFixture<PageDettesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageDettesComponent]
    });
    fixture = TestBed.createComponent(PageDettesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
