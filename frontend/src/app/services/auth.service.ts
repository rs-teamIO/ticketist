import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {User} from '../model/user.model';
import {tap} from 'rxjs/operators';
import { PORT } from '../shared/constants';

export interface IUserRegister {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
}

interface IFullToken {
  sub: string;
  audience: string;
  created: number;
  exp: number;
  authorities: string;
}

export interface IToken {
  authority: string;
  expiration: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  user = new BehaviorSubject<User>(null);
  private tokenExpirationTimer: any;
  private readonly signUpLink = `http://localhost:${PORT}/api/users`;
  private readonly loginLink = `http://localhost:${PORT}/api/auth`;

  constructor(private http: HttpClient) {}

  private handleAuthentication(token: string) {
    const parsedToken: IToken = this.parseJwt(token);
    const expirationDate = new Date(parsedToken.expiration * 1000);
    const user = new User(
      token,
      expirationDate,
      parsedToken.authority
    );
    this.user.next(user);
    this.autoLogout(parsedToken.expiration * 1000);
    localStorage.setItem('userToken', token);
  }

  signup(registerUser: IUserRegister) {
    return this.http.post<number>(
      this.signUpLink,
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
      this.loginLink,
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
    const token: string = localStorage.getItem('userToken');

    if (!token) {
      return;
    }

    const parsedToken: IToken = this.parseJwt(token);

    const loadedUser = new User(
      token,
      new Date(parsedToken.expiration * 1000),
      parsedToken.authority
    );

    if (loadedUser.token) {
      this.user.next(loadedUser);
      this.autoLogout(parsedToken.expiration * 1000);
    }

  }

  logout() {
    this.user.next(null);
    localStorage.removeItem('userToken');
    if (this.tokenExpirationTimer) {
      clearTimeout(this.tokenExpirationTimer);
    }
  }

  autoLogout(expirationDate: number) {
    console.log(`User will be logged out after ${expirationDate - new Date().getTime()}ms`);
    this.tokenExpirationTimer = setTimeout(() => {
      this.logout();
    }, expirationDate - new Date().getTime());
  }

  parseJwt = (token: string): IToken => {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map((c: any) => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const fullToken: IFullToken = JSON.parse(jsonPayload);

    return {
      authority: fullToken.authorities.split(',')[0],
      expiration: fullToken.exp
    };
  }

}
