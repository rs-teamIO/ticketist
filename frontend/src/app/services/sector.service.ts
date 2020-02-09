import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {PORT} from '../shared/constants';

export interface ISector {
  id?: number;
  name: string;
  rowsCount: number;
  columnsCount: number;
  maxCapacity: number;
  startRow?: number;
  startColumn?: number;
}


@Injectable({providedIn: 'root'})
export class SectorService {

  private readonly getSectorsByVenueIdPath = `http://localhost:${PORT}/api/sectors/venue/`;

  constructor(private http: HttpClient, private router: Router) {}

  retrieve(id: string): Observable<ISector[]> {
    return this.http.get<any>(this.getSectorsByVenueIdPath + id);
  }
}
