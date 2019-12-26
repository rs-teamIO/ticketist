import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject} from 'rxjs';
import {User} from './user.model';
import {tap} from 'rxjs/operators';

export interface RegisterUser {
  username: string;
  password: string;
  email: string;
  firstName: string;
  lastName: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {
  user = new BehaviorSubject<User>(null);

  constructor(private http: HttpClient) {
  }

  signup(registerUser: RegisterUser) {
    // Change response type from any
    return this.http.post<any>(
      'http://localhost:8000/api/users',
      {
        username: registerUser.username,
        email: registerUser.email,
        password: registerUser.password,
        firstName: registerUser.firstName,
        lastName: registerUser.lastName
      }
    ).pipe(
      tap(resData => {
        const { username, localId, token, expiresIn } = resData;
        this.handleAuthentication(username, localId, token, +expiresIn);
      })
    );
  }

  private handleAuthentication(username: string, userId: string, token: string, expiresIn: number) {
    const expirationDate = new Date(new Date().getTime() + expiresIn * 1000);
    const user = new User(
      username,
      userId,
      token,
      expirationDate
    );
    this.user.next(user);
  }

  login(username: string, password: string) {
    // Change response type from any
    return this.http.post<any>(
      'http://localhost:8000/api/auth',
      {
        username,
        password
      }
    ).pipe(
      tap(resData => {
        const { localId, token, expiresIn } = resData;
        this.handleAuthentication(resData.username, localId, token, +expiresIn);
      })
    );
  }
}
