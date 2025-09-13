import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifierUtilisateurComponent } from './modifier-utilisateur.component';

describe('ModifierUtilisateurComponent', () => {
  let component: ModifierUtilisateurComponent;
  let fixture: ComponentFixture<ModifierUtilisateurComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModifierUtilisateurComponent]
    });
    fixture = TestBed.createComponent(ModifierUtilisateurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
