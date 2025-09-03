import { TestBed } from '@angular/core/testing';

import { IndexedBonAchatService } from './indexed-bon-achat.service';

describe('IndexedBonAchatService', () => {
  let service: IndexedBonAchatService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IndexedBonAchatService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
