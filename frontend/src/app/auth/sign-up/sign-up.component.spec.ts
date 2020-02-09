import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SignUpComponent } from './sign-up.component';
import { AuthService } from 'src/app/services/auth.service';
import { CommonModule } from '@angular/common';
import { MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { IUserRegister } from '../../services/auth.service';
import { Observable, of } from 'rxjs';

describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async(() => {
    const authServiceMock = {
      signup: jasmine.createSpy('signup')
      .and.returnValue(of(5))
    };

    TestBed.configureTestingModule({
      declarations: [ SignUpComponent ],
      providers: [{provide: AuthService, useValue: authServiceMock}],
      imports: [CommonModule,
        MatCardModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        RouterTestingModule,
        BrowserAnimationsModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    authService = TestBed.get(AuthService);
    router = TestBed.get(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sign up new user', () => {
    const user: IUserRegister = {
      username: 'rocky',
      firstName: 'Rocky',
      lastName: 'Rockyic',
      email: 'rocky@gmail.com',
      password: '123456'
    };
    component.signupForm.patchValue(user);
    component.onSubmit();
    expect(authService.signup(user)).toBeTruthy();
  });

});
