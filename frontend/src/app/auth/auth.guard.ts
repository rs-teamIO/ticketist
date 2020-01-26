import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class RegisteredUserAuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Promise<boolean | UrlTree> | Observable<boolean | UrlTree> {
    return this.authService.user.pipe(
      take(1),
      map(user => {
      const isAuth = !!user;
      if (!isAuth) {
        return this.router.createUrlTree(['/login']);
      } else if (isAuth && user.authority === 'ADMIN') {
        return this.router.createUrlTree(['/']);
      } else if (isAuth && user.authority === 'REGISTERED_USER') {
        return true;
      }
    }));
  }
}

@Injectable({providedIn: 'root'})
export class AdminAuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Promise<boolean | UrlTree> | Observable<boolean | UrlTree> {
    return this.authService.user.pipe(
      take(1),
      map(user => {
      const isAuth = !!user;
      if (!isAuth) {
        return this.router.createUrlTree(['/login']);
      } else if (isAuth && user.authority === 'REGISTERED_USER') {
        return this.router.createUrlTree(['/']);
      } else if (isAuth && user.authority === 'ADMIN') {
        return true;
      }
    }));
  }
}
