import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Page} from '../model/page.model';
import {PageEvent} from '@angular/material';
import {Sector} from './sector.service';
import {tap} from "rxjs/operators";

export interface Venue {
  id: string;
  name: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
  sectors: Sector[];
  isActive: boolean;
}

export interface VenueBasic {
  name: string;
  street: string;
  city: string;
  latitude: number;
  longitude: number;
}

export interface IVenuePage {
  venues: Venue[];
  totalSize: number;
}


@Injectable({providedIn: 'root'})
export class VenueService {
  venuesChanged = new Subject<IVenuePage>();

  pageChanged = new Subject<PageEvent>();
  private page: Page = new Page(0, 2);

  constructor(private http: HttpClient) {
    this.pageChanged.subscribe((value) => {
      this.page = {page: value.pageIndex, size: value.pageSize};
    });
  }


  retrieve(): Observable<Venue[]> {
    return this.http.get<any>('http://localhost:8000/api/venues');
  }

  activate(id: string): Observable<boolean> {
    return this.http.put<boolean>('http://localhost:8000/api/venues/change-status/' + id, {});
  }

  create(venue: Venue): Observable<Venue> {
    return this.http.post<Venue>('http://localhost:8000/api/venues/', {
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

  update(id: string, venue: VenueBasic): Observable<Venue> {
    return this.http.put<any>('http://localhost:8000/api/venues/' + id, {
      name: venue.name,
      city: venue.city,
      street: venue.street,
      longitude: venue.longitude,
      latitude: venue.latitude
    });
  }

  find(id: string): Observable<Venue> {
    return this.http.get<Venue>('http://localhost:8000/api/venues/' + id);
  }

  venuesPaged() {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.get<IVenuePage>('http://localhost:8000/api/venues/paged', {params})
      .subscribe(responseData => {
        this.venuesChanged.next(responseData);
        console.log(responseData);
      });
  }
}
