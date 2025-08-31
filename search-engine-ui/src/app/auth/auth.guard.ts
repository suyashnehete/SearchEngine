import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    canActivate(route: ActivatedRouteSnapshot): Observable<boolean> | boolean {
        // First check if we have a token
        if (!this.authService.accessToken) {
            console.log('AuthGuard: No access token found, redirecting to login');
            this.router.navigate(['/login']);
            return false;
        }

        // If we have a token but authentication state is not set, validate it
        if (!this.authService.isAuthenticated) {
            console.log('AuthGuard: Token exists but not authenticated, validating token');
            return this.authService.validateToken().pipe(
                map(isValid => {
                    if (!isValid) {
                        console.log('AuthGuard: Token validation failed, redirecting to login');
                        this.router.navigate(['/login']);
                        return false;
                    }

                    // Check role requirement
                    const requiredRole = route.data['role'];
                    if (requiredRole && !this.authService.hasRole(requiredRole)) {
                        console.log('AuthGuard: Insufficient role for', requiredRole, 'user roles:', this.authService.userInfo?.authorities);
                        this.router.navigate(['/unauthorized']);
                        return false;
                    }

                    console.log('AuthGuard: Access granted');
                    return true;
                }),
                catchError(() => {
                    console.log('AuthGuard: Token validation error, redirecting to login');
                    this.router.navigate(['/login']);
                    return of(false);
                })
            );
        }

        // User is authenticated, check role requirement
        const requiredRole = route.data['role'];
        if (requiredRole && !this.authService.hasRole(requiredRole)) {
            console.log('AuthGuard: Insufficient role for', requiredRole, 'user roles:', this.authService.userInfo?.authorities);
            this.router.navigate(['/unauthorized']);
            return false;
        }

        console.log('AuthGuard: Access granted');
        return true;
    }
}