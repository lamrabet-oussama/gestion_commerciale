import { Injectable } from '@angular/core';
import {IDBPDatabase, openDB} from "idb";
import {BonAchatVenteDto} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class IndexedDBService {
  private dbPromise: Promise<IDBPDatabase<any>>;

  constructor() {
    this.dbPromise = openDB('bonVenteDB', 1, {
      upgrade(db) {
        if (!db.objectStoreNames.contains('bonVente')) {
          const store = db.createObjectStore('bonVente', { keyPath: 'id', autoIncrement: true });
          store.createIndex('serie', 'selectedSerie', { unique: false });
        }
      }
    });
  }

  async saveBon(data: BonAchatVenteDto) {
    const db = await this.dbPromise;
    // Supprime les anciennes données pour ne garder qu'une seule entrée
    const tx = db.transaction('bonVente', 'readwrite');
    await tx.objectStore('bonVente').clear();
    await tx.objectStore('bonVente').add(data);
    await tx.done;
  }

  async loadBon(): Promise<BonAchatVenteDto | null> {
    const db = await this.dbPromise;
    const tx = db.transaction('bonVente', 'readonly');
    const all = await tx.objectStore('bonVente').getAll();
    return all.length > 0 ? all[0] : null;
  }

  async clear(): Promise<void> {
    const db = await this.dbPromise;
    const tx = db.transaction('bonVente', 'readwrite');
    await tx.objectStore('bonVente').clear();
    await tx.done;
  }
}
