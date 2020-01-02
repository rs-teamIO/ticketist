import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import { Router } from '@angular/router';
import {Sector} from './sector.service';

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


@Injectable({providedIn: 'root'})
export class VenueService {

  constructor(private http: HttpClient, private router: Router) {}


  retrieve(): Observable<Venue[]> {
    return this.http.get<any>('http://localhost:8000/api/venues');
  }

  activate(id: string) {
    console.log(id);
    return this.http.get<any>('http://localhost:8000/api/venues/activate/' + id);
  }

  update(venue: Venue): Observable<Venue> {
    return this.http.post<any>('http://localhost:8000/api/venues/', {
      name: venue.name,
      city: venue.city,
      street: venue.street,
      longitude: venue.longitude,
      latitude: venue.latitude,
      sectors: venue.sectors,
      isActive: venue.isActive
    });
  }

  find(id: string): Observable<Venue> {
    return this.http.get<Venue>('http://localhost:8000/api/venues/' + id);
  }
}
