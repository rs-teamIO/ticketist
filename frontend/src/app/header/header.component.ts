import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {Subscription} from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  private userSub: Subscription;
  isAuthenticated = false;
  role = null;

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit() {
    this.userSub = this.authService.user.subscribe(user => {
      this.isAuthenticated = !!user;
      if (this.isAuthenticated) {
        this.role = user.authority;
      } else {
        this.role = null;
      }
    });
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
