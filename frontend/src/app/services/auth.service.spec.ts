import { fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PORT } from '../shared/constants';
import { AuthService, IUserRegister } from './auth.service';

describe('AuthService', () => {
  let authService: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    authService = TestBed.get(AuthService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', inject([AuthService], (service: AuthService) => {
    expect(service).toBeTruthy();
  }));

  it('sign up new user successfully', fakeAsync(() => {
    const newUser: IUserRegister = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      password: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    let resultUserId: number;
    authService.signup(newUser).subscribe((responseData: number) => {
      resultUserId = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(newUser);
    const userId = 5;
    request.flush(userId);
    tick();
    expect(resultUserId).toEqual(userId);
  }));

  it('sign up test, username already exists', fakeAsync(() => {
    const newUser: IUserRegister = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      password: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    const ret = {
      message: `Username 'rocky' is already taken`
    };

    let resultUser = null;
    authService.signup(newUser).subscribe((responseData) => {
      resultUser = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(newUser);
    request.flush(ret);

    tick();
    expect(resultUser).toEqual(ret);
  }));


  it('sign up test, username already exists', fakeAsync(() => {
    const newUser: IUserRegister = {
      username: 'rocky2',
      email: 'rocky@gmail.com',
      password: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    const ret = {
      message: `User with e-mail 'rocky@gmail.com' already registered`
    };

    let resultUser = null;
    authService.signup(newUser).subscribe((responseData) => {
      resultUser = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(newUser);
    request.flush(ret);

    tick();
    expect(resultUser).toEqual(ret);
  }));

  it('login new user successfully', () => {
    const username = 'rocky';
    const password = '123456';

    const ret = {
      token: `eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTU4MT
      IwNDQ4MDcwNSwiZXhwIjoxNTgxMjkwODgwLCJhdXRob3JpdGllcyI6IkFETUlOIn0.tvlPXByzPMAkaD2o9JcHbQFkv
      UyY1SAfJSrjXwXt13fdMVbRrXzvRcvT1-c-tLJEYEMNrTss8I79i6KV1U8yBA`
    };

    authService.login(username, password).subscribe((responseData) => {
      expect(responseData).toEqual(ret);
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/auth`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({username, password});

    request.flush(ret);
  });

});
