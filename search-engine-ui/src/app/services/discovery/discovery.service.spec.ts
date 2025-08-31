import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DiscoveryService } from './discovery.service';
import { environment } from '../../../environments/environment';

describe('DiscoveryService', () => {
    let service: DiscoveryService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [DiscoveryService]
        });
        service = TestBed.inject(DiscoveryService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should use gateway routing for auth server communication test', () => {
        service.testServiceCommunication().subscribe();

        const requests = httpMock.match(() => true);
        const authServerRequest = requests.find(req =>
            req.request.url === `${environment.apiUrl}/auth-server/actuator/health`
        );

        expect(authServerRequest).toBeTruthy();
        expect(authServerRequest?.request.url).toBe('http://localhost:8081/api/auth-server/actuator/health');
    });

    it('should test all services through gateway', () => {
        service.testServiceCommunication().subscribe();

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

    it('should get service routes through gateway', () => {
        service.getServiceRoutes().subscribe();

        const req = httpMock.expectOne(`${environment.apiUrl}/actuator/gateway/routes`);
        expect(req.request.method).toBe('GET');
        req.flush([]);
    });
});