import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import { Router } from '@angular/router';

export interface IUser {
  id: string;
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
  passwordNew: string;
  passwordNewRepeat: string;
}

export interface IUserUpdate {
  
}

@Injectable({providedIn: 'root'})
export class UserService {
  user = new BehaviorSubject<IUser>(null);

  constructor(private http: HttpClient, private router: Router) {}

  update(user: IUser): Observable<IUser> {
    return this.http.put<IUser>(
      'http://localhost:8000/api/users/',
      {
        username: user.username,
        email: user.email,
        password: user.password,
        firstName: user.firstName,
        lastName: user.lastName,
        passwordNew: user.passwordNew,
        passwordNewRepeat: user.passwordNewRepeat
      }
    );
  }

  retrieve(): Observable<any> {
    return this.http.get<any>('http://localhost:8000/api/users/me');
  }


}
