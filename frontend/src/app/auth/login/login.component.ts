import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.loginForm = new FormGroup({
      username: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
    });
  }

  onSubmit() {
    if (!this.loginForm.valid) {
      this.error = 'Wrong inputs!';
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
        this.router.navigate(['/events']);
      },
      error => {
        console.log('Error: ', error);
        this.error = error.error.message;
        this.isLoading = false;
      }
    );
  }

}
