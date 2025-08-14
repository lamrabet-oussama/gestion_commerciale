import { TestBed } from '@angular/core/testing';

import { DettesService } from './dettes.service';

describe('DettesService', () => {
  let service: DettesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DettesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
