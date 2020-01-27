import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Page} from '../model/page.model';
import {PageEvent} from '@angular/material';
import {ISector} from './sector.service';
import {tap} from 'rxjs/operators';
import { PORT } from '../shared/constants';
import {Venue} from '../model/venue.model';

export interface IVenue {
  id: number;
  name: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
  sectors: ISector[];
  isActive?: boolean;
}

export interface IVenueBasic {
  name: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
}

export interface IVenuePage {
  venues: IVenue[];
  totalSize: number;
}

@Injectable({ providedIn: 'root' })
export class VenueService {
  activeVenuesChanged = new Subject<IVenue[]>();
  venuesChanged = new Subject<IVenuePage>();
  pageChanged = new Subject<PageEvent>();
  private page: Page = new Page(0, 2);

  private readonly getActiveVenuesPath = `http://localhost:${PORT}/api/venues/active`;
  private readonly venueBasicPath = `http://localhost:${PORT}/api/venues/`;
  private readonly changeVenueStatusPath = `http://localhost:${PORT}/api/venues/change-status/`;
  private readonly venuePagePath = `http://localhost:${PORT}/api/venues/paged`;

  constructor(private http: HttpClient) {

    this.pageChanged.subscribe((value) => {
      this.page = {page: value.pageIndex, size: value.pageSize};
    });
  }

    fetchActiveVenues(): void {
    this.http
    .get<IVenue[]>(
      this.getActiveVenuesPath
    )
    .subscribe(
      (resData: any) => {
        this.activeVenuesChanged.next(resData.slice());
      },
      (error: any) => {
        console.log(error);
        this.activeVenuesChanged.next([]);
      }
    );
  }

  retrieve(): Observable<IVenue[]> {
    return this.http.get<any>(this.venueBasicPath);
  }

  activate(id: number): Observable<boolean> {
    return this.http.put<boolean>(this.changeVenueStatusPath + id, {});
  }

  create(venue: IVenue): Observable<IVenue> {
    return this.http.post<IVenue>(this.venueBasicPath, {
      id: venue.id,
      name: venue.name,
      city: venue.city,
      street: venue.street,
      longitude: venue.longitude,
      latitude: venue.latitude,
      sectors: venue.sectors,
      isActive: true
    }).pipe(tap(resData => console.log(resData)));
  }

  update(id: number, venue: IVenueBasic): Observable<IVenue> {
    return this.http.put<any>(this.venueBasicPath + id, {
      name: venue.name,
      city: venue.city,
      street: venue.street,
      longitude: venue.longitude,
      latitude: venue.latitude
    });
  }

  find(id: number): Observable<IVenue> {
    return this.http.get<IVenue>(this.venueBasicPath + id);
  }

  venuesPaged() {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.get<IVenuePage>(this.venuePagePath, {params})
      .subscribe(responseData => {
        this.venuesChanged.next(responseData);
        console.log(responseData);
      });
  }

  getVenue(venueId: number): Observable<Venue> {
    return this.http.get<Venue>(this.venueBasicPath + venueId);
  }
}
