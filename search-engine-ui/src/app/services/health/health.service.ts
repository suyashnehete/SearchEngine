import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError, timeout } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ServiceHealth {
    name: string;
    status: 'UP' | 'DOWN' | 'UNKNOWN';
    details?: any;
    responseTime?: number;
}

export interface SystemHealth {
    overall: 'UP' | 'DOWN' | 'DEGRADED';
    services: ServiceHealth[];
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class HealthService {
    private readonly services = [
        { name: 'Gateway', url: `${environment.apiUrl}/actuator/health` },
        { name: 'Auth Server', url: `${environment.apiUrl}/auth-server/actuator/health` },
        { name: 'Query Service', url: `${environment.apiUrl}/query-service/actuator/health` },
        { name: 'Crawler Service', url: `${environment.apiUrl}/crawler-service/actuator/health` },
        { name: 'Indexer Service', url: `${environment.apiUrl}/indexer-service/actuator/health` }
    ];

    constructor(private http: HttpClient) { }

    checkSystemHealth(): Observable<SystemHealth> {
        const healthChecks = this.services.map(service =>
            this.checkServiceHealth(service.name, service.url)
        );

        return forkJoin(healthChecks).pipe(
            map(services => {
                const upServices = services.filter(s => s.status === 'UP').length;
                const totalServices = services.length;

                let overall: 'UP' | 'DOWN' | 'DEGRADED';
                if (upServices === totalServices) {
                    overall = 'UP';
                } else if (upServices === 0) {
                    overall = 'DOWN';
                } else {
                    overall = 'DEGRADED';
                }

                return {
                    overall,
                    services,
                    timestamp: Date.now()
                };
            })
        );
    }

    private checkServiceHealth(name: string, url: string): Observable<ServiceHealth> {
        const startTime = Date.now();

        return this.http.get<any>(url, {
            headers: { 'Accept': 'application/json' }
        }).pipe(
            timeout(5000),
            map(response => ({
                name,
                status: response.status === 'UP' ? 'UP' as const : 'DOWN' as const,
                details: response,
                responseTime: Date.now() - startTime
            })),
            catchError(error => {
                console.warn(`Health check failed for ${name}:`, error);
                return of({
                    name,
                    status: 'DOWN' as const,
                    details: { error: error.message || 'Service unavailable' },
                    responseTime: Date.now() - startTime
                });
            })
        );
    }

    checkServiceAvailability(serviceName: string): Observable<boolean> {
        const service = this.services.find(s => s.name === serviceName);
        if (!service) {
            return of(false);
        }

        return this.checkServiceHealth(service.name, service.url).pipe(
            map(health => health.status === 'UP')
        );
    }
}