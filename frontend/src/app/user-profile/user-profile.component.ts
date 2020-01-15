import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {IUserUpdate, IRegisteredUser, UserService} from '../services/user.service';

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
      oldPassword: new FormControl(null, Validators.required),
      newPassword : new FormControl(null),
      newPasswordRepeat : new FormControl(null)
    });
    this.userService.retrieve().subscribe(
      resData => {
        this.presetForm(resData);
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

    const { email, oldPassword, lastName, firstName, username, newPassword, newPasswordRepeat } = this.userForm.value;
    const User: IUserUpdate = {
      email,
      oldPassword,
      username,
      firstName,
      lastName,
      newPassword,
      newPasswordRepeat
    };

    this.error = '';

    this.userService.update(User).subscribe(
      resData => {
        console.log(resData);
        this.presetForm(resData);
        this.isLoading = false;
      },
      error => {
        console.log('Error: ', error);
        this.error = 'An error occured!';
        this.isLoading = false;
      }
    );
  }

  private presetForm(resData: IRegisteredUser) {
    this.userForm = new FormGroup({
      username: new FormControl(resData.username, Validators.required),
      firstName: new FormControl(resData.firstName, Validators.required),
      lastName: new FormControl(resData.lastName, Validators.required),
      email: new FormControl(resData.email, [Validators.required, Validators.email]),
      oldPassword: new FormControl(null, Validators.required),
      newPassword : new FormControl(null),
      newPasswordRepeat : new FormControl(null)
    });
  }
}
