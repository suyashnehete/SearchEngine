import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        // Check if this request should skip auth interceptor processing
        const skipAuthInterceptor = req.headers.has('X-Skip-Auth-Interceptor');

        if (skipAuthInterceptor) {
            // Remove the special header and pass through without auth processing
            const cleanReq = req.clone({
                headers: req.headers.delete('X-Skip-Auth-Interceptor')
            });
            return next.handle(cleanReq);
        }

        // Add auth header if user is authenticated
        const authReq = this.addAuthHeader(req);

        return next.handle(authReq).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 401) {
                    // Check if this is an admin API call or auth-related call
                    const isAdminCall = req.url.includes('/admin/') || req.url.includes('admin');
                    const isAuthCall = req.url.includes('/auth/');
                    const isHealthCheck = req.url.includes('/actuator/health');

                    if (isAdminCall || isHealthCheck) {
                        // For admin calls and health checks, don't redirect automatically - let the component handle it
                        console.log('401 error on admin/health API call:', req.url, 'Not redirecting automatically');
                        return throwError(() => error);
                    }

                    if (isAuthCall) {
                        // For auth calls, don't try to refresh token
                        console.log('401 error on auth API call:', req.url);
                        return throwError(() => error);
                    }

                    // For other calls, try to refresh token
                    return this.handle401Error(authReq, next);
                }
                return throwError(() => error);
            })
        );
    }

    private addAuthHeader(req: HttpRequest<any>): HttpRequest<any> {
        const token = this.authService.accessToken;

        if (this.authService.isAuthenticated && token) {
            console.log('Adding auth header to request:', req.url);
            return req.clone({
                setHeaders: {
                    Authorization: `Bearer ${token}`
                }
            });
        }

        console.log('No auth header added to request:', req.url, 'Authenticated:', this.authService.isAuthenticated, 'Token:', !!token);
        return req;
    }

    private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
        console.log('Received 401 error, attempting token refresh for:', req.url);

        return this.authService.refreshToken().pipe(
            switchMap(() => {
                console.log('Token refreshed successfully, retrying request');
                const newAuthReq = this.addAuthHeader(req);
                return next.handle(newAuthReq);
            }),
            catchError((refreshError) => {
                console.log('Token refresh failed, checking if should redirect to login');
                // Only redirect to login for non-admin, non-health check calls
                const shouldRedirect = !req.url.includes('/admin/') &&
                    !req.url.includes('/actuator/health') &&
                    !req.url.includes('/auth/');

                if (shouldRedirect) {
                    console.log('Redirecting to login for:', req.url);
                    this.router.navigate(['/login']);
                } else {
                    console.log('Not redirecting to login for:', req.url);
                }
                return throwError(() => refreshError);
            })
        );
    }
}