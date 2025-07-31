import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageTiersComponent } from './page-tiers.component';

describe('PageTiersComponent', () => {
  let component: PageTiersComponent;
  let fixture: ComponentFixture<PageTiersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageTiersComponent]
    });
    fixture = TestBed.createComponent(PageTiersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
