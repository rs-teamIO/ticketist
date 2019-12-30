import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import { Router } from '@angular/router';

export interface Sector {
  id: string;
  name: string;
  rowsCount: number;
  columnsCount: number;
  startRow: number;
  startColumn: number;
  maxCapacity: number;
}


@Injectable({providedIn: 'root'})
export class SectorService {

  constructor(private http: HttpClient, private router: Router) {}

  retrieve(id: string): Observable<Sector[]> {
    return this.http.get<any>('http://localhost:8000/api/sectors/venue/' + id);
  }
}
