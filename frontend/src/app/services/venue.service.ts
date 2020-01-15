import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PORT } from '../shared/constants';

export interface IVenue {
  id: number;
  name: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
  sectors: ISector[];
}

export interface ISector {
  id: number;
  name: string;
  rowsCount: number;
  columnsCount: number;
  maxCapacity: number;
  startRow?: number;
  startColumn?: number;
}

@Injectable({ providedIn: 'root' })
export class VenueService {
  activeVenues: IVenue[] = [];
  activeVenuesChanged = new Subject<IVenue[]>();

  private readonly getActiveVenuesPath = `http://localhost:${PORT}/api/venues/active`;

  constructor(private http: HttpClient) {}

  fetchActiveVenues(): void {
    this.http
    .get<IVenue[]>(
      this.getActiveVenuesPath
    )
    .subscribe(
      (resData: any) => {
        this.activeVenues = resData;
        this.activeVenuesChanged.next(this.activeVenues.slice());
      },
      (error: any) => {
        console.log(error);
        this.activeVenues = [];
        this.activeVenuesChanged.next(this.activeVenues);
      }
    );
  }

}
