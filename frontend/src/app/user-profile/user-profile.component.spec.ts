import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {of} from 'rxjs';
import {CommonModule} from '@angular/common';
import {FlexLayoutModule} from '@angular/flex-layout';
import {ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {UserProfileComponent} from './user-profile.component';
import {IUserUpdate, UserService} from '../services/user.service';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let userService: UserService;

  beforeEach(async(() => {
    const userServiceMock = {
      update: jasmine.createSpy('update')
        .and.returnValue(of({})),
      retrieve: jasmine.createSpy('retrieve')
        .and.returnValue(of({}))
    };

    TestBed.configureTestingModule({
      declarations: [UserProfileComponent],
      providers: [{provide: UserService, useValue: userServiceMock}],
      imports: [CommonModule,
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatCardModule,
        MatInputModule,
        MatButtonModule,
        RouterTestingModule,
        BrowserAnimationsModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    userService = TestBed.get(UserService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update user', () => {
    const newUser: IUserUpdate = {
      username: 'rocky',
      email: 'rocky@gmail.com',
      oldPassword: '1234',
      newPassword: '123456',
      newPasswordRepeat: '123456',
      firstName: 'Rocky',
      lastName: 'Rockyic'
    };
    component.userForm.patchValue(newUser);
    component.onSubmit();
    expect(userService.update(newUser)).toBeTruthy();
  });

});
