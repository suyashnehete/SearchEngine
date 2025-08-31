import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface LoginRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
    expiresIn: number;
    username: string;
    authorities: string[];
}

export interface UserInfo {
    username: string;
    authorities: string[];
    enabled: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly TOKEN_KEY = 'access_token';
    private readonly REFRESH_TOKEN_KEY = 'refresh_token';
    private readonly USER_KEY = 'user_info';

    private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
    private userRolesSubject = new BehaviorSubject<string[]>([]);
    private userInfoSubject = new BehaviorSubject<UserInfo | null>(null);

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        this.initializeAuth();
    }

    private initializeAuth() {
        const token = this.getStoredToken();
        const storedUserInfo = localStorage.getItem(this.USER_KEY);

        if (token && storedUserInfo) {
            try {
                const userInfo = JSON.parse(storedUserInfo);
                this.userInfoSubject.next(userInfo);
                const roles = userInfo.authorities
                    .filter((auth: string) => auth.startsWith('ROLE_'))
                    .map((auth: string) => auth.replace('ROLE_', ''));
                this.userRolesSubject.next(roles);
                this.isAuthenticatedSubject.next(true);

                // Validate token in background
                this.validateToken().subscribe({
                    next: (isValid) => {
                        if (!isValid) {
                            console.log('Stored token is invalid, clearing auth');
                            this.clearAuth();
                        }
                    },
                    error: () => {
                        console.log('Token validation failed, clearing auth');
                        this.clearAuth();
                    }
                });
            } catch (error) {
                console.error('Error parsing stored user info:', error);
                this.clearAuth();
            }
        } else if (token) {
            // Have token but no user info, validate and load
            this.validateToken().subscribe({
                next: (isValid) => {
                    if (isValid) {
                        this.isAuthenticatedSubject.next(true);
                        this.loadUserInfo();
                    } else {
                        this.clearAuth();
                    }
                },
                error: () => this.clearAuth()
            });
        }
    }

    login(credentials: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${environment.apiUrl}/auth-server/api/auth/login`, credentials)
            .pipe(
                tap(response => {
                    this.storeTokens(response);
                    this.isAuthenticatedSubject.next(true);
                    this.loadUserInfo();
                }),
                catchError(error => {
                    console.error('Login failed:', error);
                    return throwError(() => error);
                })
            );
    }

    logout(): Observable<any> {
        return this.http.post(`${environment.apiUrl}/auth-server/api/auth/logout`, {}, {
            headers: this.getAuthHeaders()
        }).pipe(
            tap(() => {
                this.clearAuth();
                this.router.navigate(['/']);
            }),
            catchError(() => {
                // Even if logout fails on server, clear local auth
                this.clearAuth();
                this.router.navigate(['/']);
                return throwError(() => 'Logout failed');
            })
        );
    }

    refreshToken(): Observable<AuthResponse> {
        const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
        if (!refreshToken) {
            return throwError(() => 'No refresh token available');
        }

        return this.http.post<AuthResponse>(`${environment.apiUrl}/auth-server/api/auth/refresh`, {
            refreshToken: refreshToken
        }).pipe(
            tap(response => {
                this.storeTokens(response);
            }),
            catchError(error => {
                this.clearAuth();
                return throwError(() => error);
            })
        );
    }

    validateToken(): Observable<boolean> {
        return this.http.get<{ valid: boolean }>(`${environment.apiUrl}/auth-server/api/auth/validate`, {
            headers: this.getAuthHeaders()
        }).pipe(
            map(response => response.valid),
            catchError(() => {
                return throwError(() => false);
            })
        );
    }

    getCurrentUser(): Observable<UserInfo> {
        return this.http.get<UserInfo>(`${environment.apiUrl}/auth-server/api/auth/me`, {
            headers: this.getAuthHeaders()
        });
    }

    private loadUserInfo() {
        this.getCurrentUser().subscribe({
            next: (userInfo) => {
                this.userInfoSubject.next(userInfo);
                const roles = userInfo.authorities
                    .filter(auth => auth.startsWith('ROLE_'))
                    .map(auth => auth.replace('ROLE_', ''));
                this.userRolesSubject.next(roles);
                localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
            },
            error: (error) => {
                console.error('Failed to load user info:', error);
                this.clearAuth();
            }
        });
    }

    private storeTokens(response: AuthResponse) {
        localStorage.setItem(this.TOKEN_KEY, response.accessToken);
        localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);

        // Store user info
        const userInfo: UserInfo = {
            username: response.username,
            authorities: response.authorities,
            enabled: true
        };
        localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
        this.userInfoSubject.next(userInfo);

        const roles = response.authorities
            .filter(auth => auth.startsWith('ROLE_'))
            .map(auth => auth.replace('ROLE_', ''));
        this.userRolesSubject.next(roles);
    }

    private clearAuth() {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        this.isAuthenticatedSubject.next(false);
        this.userRolesSubject.next([]);
        this.userInfoSubject.next(null);
    }

    private getStoredToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    private getAuthHeaders(): HttpHeaders {
        const token = this.getStoredToken();
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });
    }

    // Public getters
    get isAuthenticated$(): Observable<boolean> {
        return this.isAuthenticatedSubject.asObservable();
    }

    get userRoles$(): Observable<string[]> {
        return this.userRolesSubject.asObservable();
    }

    get userInfo$(): Observable<UserInfo | null> {
        return this.userInfoSubject.asObservable();
    }

    get isAuthenticated(): boolean {
        return this.isAuthenticatedSubject.value && !!this.getStoredToken();
    }

    get accessToken(): string | null {
        return this.getStoredToken();
    }

    get userInfo(): UserInfo | null {
        const stored = localStorage.getItem(this.USER_KEY);
        return stored ? JSON.parse(stored) : null;
    }

    hasRole(role: string): boolean {
        const userInfo = this.userInfo;
        if (userInfo && userInfo.authorities) {
            return userInfo.authorities.includes(`ROLE_${role}`);
        }
        return false;
    }

    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    getAuthorizationHeader(): string | null {
        const token = this.getStoredToken();
        return token ? `Bearer ${token}` : null;
    }
}