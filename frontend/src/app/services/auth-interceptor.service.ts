import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpHeaders } from '@angular/common/http';
import { exhaustMap, take } from 'rxjs/operators';

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return this.authService.user.pipe(
      take(1),
      exhaustMap(user => {
        if (!user) {
          return next.handle(req);
        }
        const modifiedReq = req.clone({
          headers: new HttpHeaders({
            'Content-Type': 'application/json',
            Authorization: user.token
          })
        });
        return next.handle(modifiedReq);
      })
    );
  }
}
