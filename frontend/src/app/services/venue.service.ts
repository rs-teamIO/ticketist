import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import { Router } from '@angular/router';
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



@Injectable({providedIn: 'root'})
export class VenueService {

  constructor(private http: HttpClient, private router: Router) {}


  retrieve(): Observable<Venue[]> {
    return this.http.get<any>('http://localhost:8000/api/venues');
  }

  activate(id: string): Observable<boolean> {
    return this.http.put<boolean>('http://localhost:8000/api/venues/change-status/' + id,{});
  }

  create(venue: Venue): Observable<Venue>{
    console.log('creating');
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
}
