import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { RegisterUser, AuthService } from '../auth.service';
import {Router} from "@angular/router";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

  signupForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.signupForm = new FormGroup({
      username: new FormControl(null, Validators.required),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      password: new FormControl(null, Validators.required),
    });
  }

  onSubmit() {
    if (!this.signupForm.valid) {
      return;
    }

    this.isLoading = true;

    const { email, password, lastName, firstName, username } = this.signupForm.value;
    const newUser: RegisterUser = {
      email,
      password,
      username,
      firstName,
      lastName,
    };

    this.error = '';

    this.authService.signup(newUser).subscribe(
      resData => {
        console.log(resData);
        this.signupForm.reset();
        this.isLoading = false;
        this.router.navigate(['/events']);
      },
      error => {
        console.log('Error: ', error);
        this.error = 'An error occured!';
        this.isLoading = false;
      }
    );
  }

}
