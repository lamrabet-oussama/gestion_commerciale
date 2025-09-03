import { Injectable } from '@angular/core';
import {IDBPDatabase, openDB} from "idb";
import {BonAchatVenteDto} from "../../api-client";

@Injectable({
  providedIn: 'root'
})
export class IndexedBonAchatService {

  private dbPromise: Promise<IDBPDatabase<any>>;

  constructor() {
    this.dbPromise = openDB('bonAchatDB', 1, {
      upgrade(db) {
        if (!db.objectStoreNames.contains('bonAchat')) {
          const store = db.createObjectStore('bonAchat', { keyPath: 'id', autoIncrement: true });
          store.createIndex('serieAchat', 'selectedAchatSerie', { unique: false });
        }
      }
    });
  }

  async saveBon(data: BonAchatVenteDto) {
    const db = await this.dbPromise;
    // Supprime les anciennes données pour ne garder qu'une seule entrée
    const tx = db.transaction('bonAchat', 'readwrite');
    await tx.objectStore('bonAchat').clear();
    await tx.objectStore('bonAchat').add(data);
    await tx.done;
  }

  async loadBon(): Promise<BonAchatVenteDto | null> {
    const db = await this.dbPromise;
    const tx = db.transaction('bonAchat', 'readonly');
    const all = await tx.objectStore('bonAchat').getAll();
    return all.length > 0 ? all[0] : null;
  }

  async clear(): Promise<void> {
    const db = await this.dbPromise;
    const tx = db.transaction('bonAchat', 'readwrite');
    await tx.objectStore('bonAchat').clear();
    await tx.done;
  }}
