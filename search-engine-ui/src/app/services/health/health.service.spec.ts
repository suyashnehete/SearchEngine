import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HealthService } from './health.service';
import { environment } from '../../../environments/environment';

describe('HealthService', () => {
    let service: HealthService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [HealthService]
        });
        service = TestBed.inject(HealthService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should use gateway routing for auth server health check', () => {
        service.checkSystemHealth().subscribe();

        const requests = httpMock.match(() => true);
        const authServerRequest = requests.find(req =>
            req.request.url === `${environment.apiUrl}/auth-server/actuator/health`
        );

        expect(authServerRequest).toBeTruthy();
        expect(authServerRequest?.request.url).toBe('http://localhost:8081/api/auth-server/actuator/health');
    });

    it('should check all services through gateway', () => {
        service.checkSystemHealth().subscribe();

        const expectedUrls = [
            `${environment.apiUrl}/actuator/health`,
            `${environment.apiUrl}/auth-server/actuator/health`,
            `${environment.apiUrl}/query-service/actuator/health`,
            `${environment.apiUrl}/crawler-service/actuator/health`,
            `${environment.apiUrl}/indexer-service/actuator/health`
        ];

        expectedUrls.forEach(url => {
            const req = httpMock.expectOne(url);
            req.flush({ status: 'UP' });
        });
    });
});