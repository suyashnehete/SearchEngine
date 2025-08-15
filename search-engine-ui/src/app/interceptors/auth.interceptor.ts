import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        if (this.authService.isAuthenticated) {
            const authReq = req.clone({
                setHeaders: {
                    Authorization: `Bearer ${this.authService.accessToken}`
                }
            });
            return next.handle(authReq);
        }

        return next.handle(req);
    }
}