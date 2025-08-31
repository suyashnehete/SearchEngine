import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ServiceInstance {
    instanceId: string;
    app: string;
    ipAddr: string;
    port: number;
    status: string;
    healthCheckUrl: string;
    statusPageUrl: string;
    homePageUrl: string;
}

export interface EurekaApplication {
    name: string;
    instances: ServiceInstance[];
}

export interface ServiceDiscoveryStatus {
    eurekaAvailable: boolean;
    registeredServices: EurekaApplication[];
    serviceCount: number;
    healthyServices: number;
    timestamp: number;
}

@Injectable({
    providedIn: 'root'
})
export class DiscoveryService {
    private readonly eurekaUrl = 'http://localhost:8761';

    constructor(private http: HttpClient) { }

    getServiceDiscoveryStatus(): Observable<ServiceDiscoveryStatus> {
        return this.getRegisteredServices().pipe(
            map(applications => {
                const healthyServices = applications.reduce((count, app) => {
                    return count + app.instances.filter(instance => instance.status === 'UP').length;
                }, 0);

                const totalInstances = applications.reduce((count, app) => count + app.instances.length, 0);

                return {
                    eurekaAvailable: true,
                    registeredServices: applications,
                    serviceCount: totalInstances,
                    healthyServices,
                    timestamp: Date.now()
                };
            }),
            catchError(error => {
                console.error('Failed to get service discovery status:', error);
                return of({
                    eurekaAvailable: false,
                    registeredServices: [],
                    serviceCount: 0,
                    healthyServices: 0,
                    timestamp: Date.now()
                });
            })
        );
    }

    private getRegisteredServices(): Observable<EurekaApplication[]> {
        return this.http.get<any>(`${this.eurekaUrl}/eureka/apps`, {
            headers: { 'Accept': 'application/json' }
        }).pipe(
            map(response => {
                if (!response.applications || !response.applications.application) {
                    return [];
                }

                const apps = Array.isArray(response.applications.application)
                    ? response.applications.application
                    : [response.applications.application];

                return apps.map((app: any) => ({
                    name: app.name,
                    instances: Array.isArray(app.instance) ? app.instance : [app.instance]
                }));
            }),
            catchError(error => {
                console.error('Failed to fetch registered services:', error);
                return of([]);
            })
        );
    }

    testServiceCommunication(): Observable<{ [serviceName: string]: boolean }> {
        const services = [
            { name: 'gateway', url: `${environment.apiUrl}/actuator/health` },
            { name: 'auth-server', url: `${environment.apiUrl}/auth-server/actuator/health` },
            { name: 'query-service', url: `${environment.apiUrl}/query-service/actuator/health` },
            { name: 'crawler-service', url: `${environment.apiUrl}/crawler-service/actuator/health` },
            { name: 'indexer-service', url: `${environment.apiUrl}/indexer-service/actuator/health` }
        ];

        const tests = services.map(service =>
            this.testSingleService(service.name, service.url)
        );

        return forkJoin(tests).pipe(
            map(results => {
                const communicationStatus: { [serviceName: string]: boolean } = {};
                results.forEach(result => {
                    communicationStatus[result.name] = result.success;
                });
                return communicationStatus;
            })
        );
    }

    private testSingleService(name: string, url: string): Observable<{ name: string, success: boolean }> {
        return this.http.get(url).pipe(
            map(() => ({ name, success: true })),
            catchError(() => of({ name, success: false }))
        );
    }

    getServiceRoutes(): Observable<any> {
        return this.http.get(`${environment.apiUrl}/actuator/gateway/routes`, {
            headers: { 'Accept': 'application/json' }
        }).pipe(
            catchError(error => {
                console.error('Failed to get gateway routes:', error);
                return of([]);
            })
        );
    }

    testLoadBalancing(serviceName: string): Observable<{ balanced: boolean, instances: string[] }> {
        // This would test if requests are being load balanced across multiple instances
        // For now, we'll just check if multiple instances are registered
        return this.getRegisteredServices().pipe(
            map(applications => {
                const service = applications.find(app =>
                    app.name.toLowerCase() === serviceName.toLowerCase()
                );

                if (!service) {
                    return { balanced: false, instances: [] };
                }

                const healthyInstances = service.instances
                    .filter(instance => instance.status === 'UP')
                    .map(instance => `${instance.ipAddr}:${instance.port}`);

                return {
                    balanced: healthyInstances.length > 1,
                    instances: healthyInstances
                };
            })
        );
    }
}