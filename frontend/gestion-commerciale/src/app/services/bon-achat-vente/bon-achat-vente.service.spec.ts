import { TestBed } from '@angular/core/testing';

import { BonAchatVenteService } from './bon-achat-vente.service';

describe('BonAchatVenteService', () => {
  let service: BonAchatVenteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BonAchatVenteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
