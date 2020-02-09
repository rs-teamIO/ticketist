import {fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {IRegisteredUser, IUserUpdate, UserService} from './user.service';
import {PORT} from '../shared/constants';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.get(UserService);
    httpMock = TestBed.get(HttpTestingController);

  });


  it('should be created', inject([UserService], (service1: UserService) => {
    expect(service1).toBeTruthy();
  }));

  it('retrieve user successfully', () => {
    const registeredUser: IRegisteredUser = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    service.retrieve().subscribe((responseData: IRegisteredUser) => {
      expect(responseData).toEqual(registeredUser);
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users/me`);
    expect(request.request.method).toBe('GET');
    request.flush(registeredUser);
  });

  it('update user fails when usernames dont match', () => {
    const newUser: IUserUpdate = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      oldPassword: '123456',
      newPassword: '123456',
      newPasswordRepeat: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    const ret = {
      message: `Usernames don't match.`
    };

    let resultUser = null;
    service.update(newUser).subscribe((responseData) => {
      resultUser = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(newUser);
    request.flush(ret);

    expect(resultUser).toEqual(ret);
  });

  it('update user throws password is incorrect', () => {
    const newUser: IUserUpdate = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      oldPassword: '1234',
      newPassword: '123456',
      newPasswordRepeat: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };

    const ret = {
      message: `Incorrect password.`
    };

    let resultUser = null;
    service.update(newUser).subscribe((responseData) => {
      resultUser = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/users`);
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(newUser);
    request.flush(ret);

    expect(resultUser).toEqual(ret);
  });


});
