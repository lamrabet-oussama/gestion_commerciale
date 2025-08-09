import { TestBed } from '@angular/core/testing';

import { CaisseJourService } from './caisse-jour.service';

describe('CaisseJourService', () => {
  let service: CaisseJourService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CaisseJourService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
