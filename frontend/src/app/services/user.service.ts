import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import { Router } from '@angular/router';

export interface IUserUpdate {
  username: string;
  oldPassword: string;
  email: string;
  firstName: string;
  lastName: string;
  newPassword: string;
  newPasswordRepeat: string;
}

export interface IRegisteredUser {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}

@Injectable({providedIn: 'root'})
export class UserService {
  user = new BehaviorSubject<IRegisteredUser>(null);

  constructor(private http: HttpClient, private router: Router) {}

  update(user: IUserUpdate): Observable<IRegisteredUser> {
    return this.http.put<IRegisteredUser>(
      'http://localhost:8000/api/users',
      {
        username: user.username,
        email: user.email,
        oldPassword: user.oldPassword,
        firstName: user.firstName,
        lastName: user.lastName,
        newPassword: user.newPassword,
        newPasswordRepeat: user.newPasswordRepeat
      }
    );
  }

  retrieve(): Observable<any> {
    return this.http.get<any>('http://localhost:8000/api/users/me');
  }


}
