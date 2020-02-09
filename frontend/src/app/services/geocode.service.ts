import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class GeocodeService {

  constructor(private http: HttpClient) {
  }

  gelocate(address: string): Observable<any> {
    return this.http.get<any>('https://cors-anywhere.herokuapp.com/https://maps.googleapis.com/maps/api/geocode/json?address=' + address +
      '&key=AIzaSyCn40xrisQWoIytZzAEohvAWPQfTIk1SR4');
  }

}
