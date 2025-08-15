import { Injectable } from '@angular/core';
import { OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { authConfig } from './auth.config';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    private userRolesSubject = new BehaviorSubject<string[]>([]);

    constructor(
        private oauthService: OAuthService,
        private router: Router
    ) {
        this.configureOAuth();
    }

    private configureOAuth() {
        this.oauthService.configure(authConfig);
        this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
            if (this.oauthService.hasValidAccessToken()) {
                this.isAuthenticatedSubject.next(true);
                this.loadUserRoles();
            }
        });
    }

    login() {
        this.oauthService.initCodeFlow();
    }

    logout() {
        this.oauthService.logOut();
        this.isAuthenticatedSubject.next(false);
        this.userRolesSubject.next([]);
        this.router.navigate(['/']);
    }

    get isAuthenticated$(): Observable<boolean> {
        return this.isAuthenticatedSubject.asObservable();
    }

    get userRoles$(): Observable<string[]> {
        return this.userRolesSubject.asObservable();
    }

    get isAuthenticated(): boolean {
        return this.oauthService.hasValidAccessToken();
    }

    get accessToken(): string {
        return this.oauthService.getAccessToken();
    }

    get userClaims(): any {
        return this.oauthService.getIdentityClaims();
    }

    hasRole(role: string): boolean {
        const claims = this.oauthService.getIdentityClaims() as any;
        if (claims && claims.authorities) {
            return claims.authorities.includes(`ROLE_${role}`);
        }
        return false;
    }

    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    private loadUserRoles() {
        const claims = this.oauthService.getIdentityClaims() as any;
        if (claims && claims.authorities) {
            const roles = claims.authorities
                .filter((auth: string) => auth.startsWith('ROLE_'))
                .map((auth: string) => auth.replace('ROLE_', ''));
            this.userRolesSubject.next(roles);
        }
    }
}