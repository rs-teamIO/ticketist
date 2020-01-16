import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import { Router } from '@angular/router';
import { PORT } from '../shared/constants';

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
  private readonly getUsersInfoPath = `http://localhost:${PORT}/api/users/me`;
  private readonly usersBasicPath = `http://localhost:${PORT}/api/users`;

  constructor(private http: HttpClient, private router: Router) {}

  update(user: IUserUpdate): Observable<IRegisteredUser> {
    return this.http.put<IRegisteredUser>(
      this.usersBasicPath,
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
    return this.http.get<any>(this.getUsersInfoPath);
  }


}
