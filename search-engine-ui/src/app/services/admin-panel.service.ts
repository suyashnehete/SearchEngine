import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { timeout, catchError, map } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class AdminPanelService {
    constructor(private http: HttpClient, private authService: AuthService) { }

    makeAdminRequest<T>(method: 'GET' | 'POST' | 'DELETE', path: string, body?: any): Observable<T> {
        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'X-Admin-Request': 'true'
        });
        const options = { headers };
        const fullUrl = `${environment.apiUrl}/${path}`;
        switch (method) {
            case 'GET':
                return this.http.get<T>(fullUrl, options);
            case 'POST':
                return this.http.post<T>(fullUrl, body || {}, options);
            case 'DELETE':
                return this.http.delete<T>(fullUrl, options);
            default:
                throw new Error(`Unsupported method: ${method}`);
        }
    }

    checkServiceHealth(service: { name: string, url: string, port: string }): Observable<string> {
        const headers = new HttpHeaders({
            'X-Skip-Auth-Interceptor': 'true',
            'Content-Type': 'application/json'
        });
        return this.http.get(service.url, { headers }).pipe(
            timeout(5000),
            catchError((err: any) => {
                let status = 'Not running';
                if (err.status === 0 || err.name === 'TimeoutError') {
                    status = 'Cannot connect - service may be down';
                } else if (err.status === 404) {
                    status = 'Service running but health endpoint not found';
                } else if (err.status === 401 || err.status === 403) {
                    status = 'Running (auth required for health endpoint)';
                } else if (err.status >= 500) {
                    status = 'Service error - check logs';
                }
                return of(`❌ ${service.name} (port ${service.port}): ${status}`);
            }),
            map((data: any) => `✅ ${service.name} (port ${service.port}): Running`)
        );
    }

    checkBackendAvailability(): Observable<boolean> {
        const headers = new HttpHeaders({
            'X-Skip-Auth-Interceptor': 'true',
            'Content-Type': 'application/json'
        });
        return this.http.get(`${environment.apiUrl}/actuator/health`, { headers }).pipe(
            timeout(3000),
            map(() => true),
            catchError(() => of(false))
        );
    }
}
