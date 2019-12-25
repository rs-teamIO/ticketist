import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { RegisterUser, AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      username: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
    });
  }

  onSubmit() {
    if (!this.loginForm.valid) {
      return;
    }

    this.isLoading = true;

    const { password, username } = this.loginForm.value;
    this.error = '';

    this.authService.login(username, password).subscribe(
      resData => {
        console.log(resData);
        this.loginForm.reset();
        this.isLoading = false;
      },
      error => {
        console.log('Error: ', error);
        this.error = 'An error occured!';
        this.isLoading = false;
      }
    );
  }

}
