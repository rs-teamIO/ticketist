import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {IUser, UserService} from '../services/user.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  userForm: FormGroup;
  isLoading = false;
  error = '';

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit() {
    this.userForm = new FormGroup({
      username: new FormControl(null, Validators.required),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      password: new FormControl(null, Validators.required),
      passwordNew : new FormControl(null),
      passwordNewRepeat : new FormControl(null)
    });
    this.userService.retrieve().subscribe(
      resData => {
        console.log(resData);
      },
      error => {
        console.log('Error ', error);
        this.error = 'An error occoured';
        this.isLoading = false;
      }
    );
  }

  onSubmit() {
    if (!this.userForm.valid) {
      this.error = 'Wrong inputs!';
      return;
    }

    this.isLoading = true;

    const { id, email, password, lastName, firstName, username, passwordNew, passwordNewRepeat } = this.userForm.value;
    const User: IUser = {
      id,
      email,
      password,
      username,
      firstName,
      lastName,
      passwordNew,
      passwordNewRepeat
    };

    this.error = '';

    this.userService.update(User).subscribe(
      resData => {
        console.log(resData);
        this.userForm.reset();
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
