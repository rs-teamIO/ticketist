import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import { Router } from '@angular/router';

export interface IUserRegister {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {
  user = new BehaviorSubject<User>(null);
  private tokenExpirationTimer: any;

  constructor(private http: HttpClient, private router: Router) {}

  private handleAuthentication(token: string, expiresIn: number = 86400) {
    const expirationDate = new Date(new Date().getTime() + expiresIn * 1000);
    const user = new User(
      token,
      expirationDate
    );
    this.user.next(user);
    this.autoLogout(expiresIn * 1000);
    localStorage.setItem('userData', JSON.stringify(user));
  }

  signup(registerUser: IUserRegister) {
    return this.http.post<number>(
      'http://localhost:8000/api/users',
      {
        username: registerUser.username,
        email: registerUser.email,
        password: registerUser.password,
        firstName: registerUser.firstName,
        lastName: registerUser.lastName
      }
    );
  }

  login(username: string, password: string) {
    return this.http.post<{token: string}>(
      'http://localhost:8000/api/auth',
      {
        username,
        password
      }
    ).pipe(
      tap(resData => {
        this.handleAuthentication(resData.token);
      })
    );
  }

  autoLogin() {
    const userData: {
      _token: string,
      _tokenExpirationDate: string
    }  = JSON.parse(localStorage.getItem('userData'));

    if (!userData) {
      return;
    }

    const loadedUser = new User(
      userData._token,
      new Date(userData._tokenExpirationDate)
    );

    if (loadedUser.token) {
      this.user.next(loadedUser);
      const expirationDuration = new Date(userData._tokenExpirationDate).getTime() - new Date().getTime();
      this.autoLogout(expirationDuration);
    }

  }

  logout() {
    this.user.next(null);
    localStorage.removeItem('userData');
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
    }
    this.router.navigate(['/login']);
  }

  autoLogout(expirationDuration: number) {
    console.log(`User will be logged out after ${expirationDuration}ms`);
    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
    }, expirationDuration);
  }

}
