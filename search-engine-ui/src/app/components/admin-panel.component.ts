import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../environments/environment';
import { timeout, catchError, map } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-admin-panel',
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-12">
          <h2><i class="fas fa-cogs me-2"></i>Admin Panel</h2>
          <p class="text-muted">Manage indexing operations</p>
          <div class="alert alert-info">
            <h6><i class="fas fa-info-circle me-2"></i>Service Status</h6>
            <p class="mb-2">Admin panel uses API Gateway with JWT authentication. Ensure all backend services are running:</p>
            <small>
              <strong>Required Services:</strong><br>
              • Gateway (port 8081) - API routing<br>
              • Auth Server (port 8080) - Authentication<br>
              • Crawler (port 8082) - Web crawling<br>
              • Indexer (port 8083) - Content indexing<br>
              • Query (port 8084) - Search processing<br>
              • Discovery (port 8761) - Service registry
            </small>
            <div class="mt-2">
              <button class="btn btn-sm btn-outline-primary me-2" (click)="testConnection()">
                <i class="fas fa-heartbeat me-1"></i>Check Service Status
              </button>
              <button class="btn btn-sm btn-outline-secondary me-2" (click)="debugAuth()">
                <i class="fas fa-bug me-1"></i>Debug Auth
              </button>
              <button class="btn btn-sm btn-outline-warning me-2" (click)="quickTest()">
                <i class="fas fa-flask me-1"></i>Quick Test
              </button>
              <button class="btn btn-sm btn-outline-success me-2" (click)="testAdminEndpoint()">
                <i class="fas fa-vial me-1"></i>Test Admin API
              </button>
              <button class="btn btn-sm btn-outline-danger me-2" (click)="checkSystemReadiness()">
                <i class="fas fa-clipboard-check me-1"></i>System Check
              </button>
              <a class="btn btn-sm btn-outline-info" routerLink="/startup-guide">
                <i class="fas fa-rocket me-1"></i>Startup Guide
              </a>tton>
            </div>
          </div>
        </div>
      </div>
      
      <div class="row mt-4">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5><i class="fas fa-spider me-2"></i>Crawler Management</h5>
            </div>
            <div class="card-body">
              <p>Control crawler service and queue management.</p>
              <button class="btn btn-success me-2" (click)="startCrawler()">
                <i class="fas fa-play me-1"></i>
                Start Service
              </button>
              <button class="btn btn-danger me-2" (click)="stopCrawler()">
                <i class="fas fa-stop me-1"></i>
                Stop Service
              </button>
              <button class="btn btn-info me-2" (click)="viewCrawlerQueue()">
                <i class="fas fa-list me-1"></i>
                View Queue
              </button>
              <button class="btn btn-secondary" (click)="testConnection()">
                <i class="fas fa-heartbeat me-1"></i>
                Test Connection
              </button>
            </div>
          </div>
        </div>
        
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5><i class="fas fa-database me-2"></i>Index Management</h5>
            </div>
            <div class="card-body">
              <p>Manage search index and optimization.</p>
              <button class="btn btn-primary me-2" (click)="reindexAll()">
                <i class="fas fa-sync me-1"></i>
                Reindex All
              </button>
              <button class="btn btn-warning me-2" (click)="optimizeIndex()">
                <i class="fas fa-tachometer-alt me-1"></i>
                Optimize
              </button>
              <button class="btn btn-danger" (click)="clearIndex()">
                <i class="fas fa-trash me-1"></i>
                Clear Index
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="row mt-4">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5><i class="fas fa-shield-alt me-2"></i>Content Moderation</h5>
            </div>
            <div class="card-body">
              <p>Manage blocked domains and content filtering.</p>
              <button class="btn btn-secondary me-2" (click)="viewBlockedDomains()">
                <i class="fas fa-ban me-1"></i>
                Blocked Domains
              </button>
              <button class="btn btn-info" (click)="viewCrawlPolicies()">
                <i class="fas fa-file-alt me-1"></i>
                Crawl Policies
              </button>
            </div>
          </div>
        </div>
        
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5><i class="fas fa-chart-line me-2"></i>Analytics & Monitoring</h5>
            </div>
            <div class="card-body">
              <p>View system performance and usage analytics.</p>
              <button class="btn btn-info me-2" (click)="viewSystemMetrics()">
                <i class="fas fa-chart-bar me-1"></i>
                System Metrics
              </button>
              <button class="btn btn-primary" (click)="viewSearchAnalytics()">
                <i class="fas fa-search me-1"></i>
                Search Analytics
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="row mt-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5><i class="fas fa-chart-bar me-2"></i>System Overview</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-3">
                  <div class="text-center">
                    <h6>Crawler Status</h6>
                    <span class="badge bg-success">Running</span>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="text-center">
                    <h6>Queue Size</h6>
                    <span class="badge bg-warning">45</span>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="text-center">
                    <h6>Documents Indexed</h6>
                    <span class="badge bg-info">1,234</span>
                  </div>
                </div>
                <div class="col-md-3">
                  <div class="text-center">
                    <h6>Index Size</h6>
                    <span class="badge bg-primary">2.4 GB</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AdminPanelComponent implements OnInit {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  ngOnInit() {
    console.log('Admin Panel - Auth Status:', {
      isAuthenticated: this.authService.isAuthenticated,
      hasToken: !!this.authService.accessToken,
      token: this.authService.accessToken?.substring(0, 20) + '...',
      userInfo: this.authService.userInfo,
      isAdmin: this.authService.isAdmin()
    });

    // Additional debug info
    if (!this.authService.isAuthenticated) {
      console.warn('User is not authenticated but accessed admin panel');
    }
    if (!this.authService.isAdmin()) {
      console.warn('User does not have admin role but accessed admin panel');
    }

    // Show initial status
    this.showInitialStatus();
  }

  private showInitialStatus() {
    const authStatus = this.authService.isAuthenticated && this.authService.isAdmin();
    console.log('Admin Panel Ready - Auth Status:', authStatus);
  }

  // Admin request method - uses API Gateway with JWT authentication
  private makeAdminRequest<T>(method: 'GET' | 'POST' | 'DELETE', path: string, body?: any): Observable<T> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Admin-Request': 'true'
    });

    const options = { headers };
    const fullUrl = `${environment.apiUrl}/${path}`;

    console.log(`Making ${method} request to: ${fullUrl}`, {
      authenticated: this.authService.isAuthenticated,
      hasToken: !!this.authService.accessToken,
      isAdmin: this.authService.isAdmin()
    });

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

  startCrawler() {
    console.log('Starting crawler...');
    this.executeAdminAction('POST', 'crawler-service/admin/start', null, 'Crawler started successfully!');
  }

  stopCrawler() {
    console.log('Stopping crawler...');
    this.executeAdminAction('POST', 'crawler-service/admin/stop', null, 'Crawler stopped successfully!');
  }

  reindexAll() {
    console.log('Starting reindex...');
    this.executeAdminAction('POST', 'indexer-service/admin/reindex', null, 'Reindexing started successfully!');
  }

  clearIndex() {
    if (confirm('Are you sure you want to clear the entire index? This action cannot be undone.')) {
      console.log('Clearing index...');
      this.executeAdminAction('DELETE', 'indexer-service/admin/index', null, 'Index cleared successfully!');
    }
  }

  optimizeIndex() {
    console.log('Optimizing index...');
    this.executeAdminAction('POST', 'indexer-service/admin/optimize', null, 'Index optimization started successfully!');
  }

  viewCrawlerQueue() {
    console.log('Fetching crawler queue...');
    this.executeAdminAction('GET', 'crawler-service/admin/queue', null, 'Crawler queue fetched successfully! Check console for details.');
  }

  viewBlockedDomains() {
    // This would be implemented later
    console.log('Blocked domains feature coming soon');
    alert('Blocked domains feature coming soon!\n\nThis will allow you to manage domains that should not be crawled.');
  }

  viewCrawlPolicies() {
    // This would be implemented later
    console.log('Crawl policies feature coming soon');
    alert('Crawl policies feature coming soon!\n\nThis will allow you to configure crawling rules and restrictions.');
  }

  viewSystemMetrics() {
    console.log('Fetching system metrics...');
    this.executeAdminAction('GET', 'crawler-service/admin/stats', null, 'Crawler stats fetched successfully!');
    this.executeAdminAction('GET', 'indexer-service/admin/stats', null, 'Index stats fetched successfully!');
  }

  viewSearchAnalytics() {
    // This would be implemented later with query service analytics
    console.log('Search analytics feature coming soon');
    alert('Search analytics feature coming soon!');
  }

  // Comprehensive system check
  checkSystemReadiness() {
    console.log('Performing comprehensive system check...');

    const checks = [
      { name: 'Authentication', check: () => this.authService.isAuthenticated },
      { name: 'Admin Role', check: () => this.authService.isAdmin() },
      { name: 'Access Token', check: () => !!this.authService.accessToken }
    ];

    const results = checks.map(check => ({
      name: check.name,
      status: check.check(),
      icon: check.check() ? '✅' : '❌'
    }));

    const authResults = results.map(r => `${r.icon} ${r.name}: ${r.status ? 'OK' : 'FAILED'}`).join('\n');

    const message = `System Readiness Check:\n\n${authResults}\n\n` +
      `Next Steps:\n` +
      `1. If auth is OK, click "Test Admin API" to check backend\n` +
      `2. If backend fails, start services using startup guide\n` +
      `3. If all OK, admin features should work`;

    alert(message);
  }

  testConnection() {
    console.log('Testing backend connections...');

    // Test multiple services with special headers to bypass auth interceptor
    const services = [
      { name: 'Gateway', url: `${environment.apiUrl}/actuator/health`, port: '8081' },
      { name: 'Auth Server', url: `${environment.apiUrl}/auth-server/actuator/health`, port: '8080' },
      { name: 'Crawler Service', url: `${environment.apiUrl}/crawler-service/actuator/health`, port: '8082' },
      { name: 'Indexer Service', url: `${environment.apiUrl}/indexer-service/actuator/health`, port: '8083' },
      { name: 'Query Service', url: `${environment.apiUrl}/query-service/actuator/health`, port: '8084' }
    ];

    let results: string[] = [];
    let completed = 0;

    services.forEach(service => {
      this.checkServiceHealth(service).subscribe({
        next: (result) => {
          results.push(result);
          completed++;
          if (completed === services.length) {
            this.showConnectionResults(results);
          }
        }
      });
    });
  }

  private checkServiceHealth(service: { name: string, url: string, port: string }): Observable<string> {
    // Use special headers to bypass auth interceptor for health checks
    const headers = new HttpHeaders({
      'X-Skip-Auth-Interceptor': 'true',
      'Content-Type': 'application/json'
    });

    return this.http.get(service.url, { headers }).pipe(
      timeout(5000), // 5 second timeout
      catchError((err: any) => {
        console.error(`${service.name} (${service.port}): Failed`, err);
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
      map((data: any) => {
        console.log(`${service.name} (${service.port}): OK`, data);
        return `✅ ${service.name} (port ${service.port}): Running`;
      })
    );
  }

  private showConnectionResults(results: string[]) {
    const authStatus = `Authentication Status:\n` +
      `• Authenticated: ${this.authService.isAuthenticated ? '✅' : '❌'}\n` +
      `• Has Token: ${!!this.authService.accessToken ? '✅' : '❌'}\n` +
      `• Admin Role: ${this.authService.isAdmin() ? '✅' : '❌'}\n\n`;

    const message = authStatus + 'Service Status Check:\n\n' + results.join('\n') +
      '\n\nNote: Services should be running on their respective ports for admin features to work.';
    alert(message);
  }

  // Debug method to test auth without triggering login popup
  debugAuth() {
    console.log('=== Authentication Debug Info ===');
    console.log('isAuthenticated:', this.authService.isAuthenticated);
    console.log('accessToken:', this.authService.accessToken);
    console.log('userInfo:', this.authService.userInfo);
    console.log('isAdmin:', this.authService.isAdmin());
    console.log('localStorage token:', localStorage.getItem('access_token'));
    console.log('localStorage userInfo:', localStorage.getItem('user_info'));

    const debugInfo = `Authentication Debug:\n\n` +
      `• Authenticated: ${this.authService.isAuthenticated}\n` +
      `• Has Token: ${!!this.authService.accessToken}\n` +
      `• Token Length: ${this.authService.accessToken?.length || 0}\n` +
      `• User: ${this.authService.userInfo?.username || 'None'}\n` +
      `• Roles: ${this.authService.userInfo?.authorities?.join(', ') || 'None'}\n` +
      `• Admin Role: ${this.authService.isAdmin()}`;

    alert(debugInfo);
  }

  // Quick test method that doesn't make any API calls
  quickTest() {
    const authOk = this.authService.isAuthenticated && this.authService.isAdmin();
    const message = authOk ?
      '✅ Authentication is working correctly!\n\nYou are logged in as an admin. Admin features should work if backend services are running.\n\nClick "Check Service Status" to verify backend availability.' :
      '❌ Authentication issue detected!\n\nYou are not properly authenticated as an admin. Please refresh the page and log in again.';

    alert(message);
  }

  // Simple test of admin functionality without complex checks
  testAdminEndpoint() {
    console.log('Testing simple admin endpoint...');

    this.makeAdminRequest('GET', 'crawler-service/admin/stats').subscribe({
      next: (data) => {
        console.log('Admin endpoint test successful:', data);
        alert('✅ Admin endpoint test successful!\n\nBackend services are running and responding.');
      },
      error: (err) => {
        console.error('Admin endpoint test failed:', err);
        if (err.status === 0) {
          alert('❌ Backend services are not running.\n\nPlease start the services using the startup guide.');
        } else if (err.status === 401) {
          alert('❌ Authentication failed.\n\nYou may need to log in again or check your admin privileges.');
        } else if (err.status === 403) {
          alert('❌ Access denied.\n\nAdmin privileges are required for this operation.');
        } else {
          alert(`❌ Admin endpoint test failed.\n\nStatus: ${err.status}\nError: ${err.message || 'Unknown error'}`);
        }
      }
    });
  }

  // Simple backend availability check
  private checkBackendAvailability(): Observable<boolean> {
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

  // Unified admin action executor with proper error handling
  private executeAdminAction<T>(method: 'GET' | 'POST' | 'DELETE', path: string, body?: any, successMessage?: string): void {
    // Check authentication first
    if (!this.authService.isAuthenticated || !this.authService.isAdmin()) {
      alert('Authentication Error: You must be logged in as an admin. Please refresh the page and log in again.');
      return;
    }

    // Make the request directly (since backend has permitAll for now)
    this.makeAdminRequest<T>(method, path, body).subscribe({
      next: (data) => {
        console.log(`${method} ${path} successful:`, data);
        if (successMessage) {
          alert(successMessage);
        }
        if (data) {
          console.log('Response data:', data);
        }
      },
      error: (err) => {
        console.error(`${method} ${path} failed:`, err);
        this.handleAdminError(err, path);
      }
    });
  }

  // Centralized error handling for admin operations
  private handleAdminError(error: any, path: string): void {
    let errorMessage = 'Unknown error occurred';

    if (error.status === 0) {
      errorMessage = 'Cannot connect to backend services. Please ensure all services are running:\n\n' +
        '• Gateway (port 8081)\n' +
        '• Crawler (port 8082)\n' +
        '• Indexer (port 8083)\n' +
        '• Query (port 8084)\n\n' +
        'Use the "Check Service Status" button to verify.';
    } else if (error.status === 404) {
      errorMessage = `Endpoint not found: ${path}\n\nThe service may not be running or the endpoint may not exist.`;
    } else if (error.status === 401) {
      errorMessage = 'Authentication failed. Please refresh the page and log in again.';
    } else if (error.status === 403) {
      errorMessage = 'Access denied. Admin privileges required.';
    } else if (error.status >= 500) {
      errorMessage = `Server error (${error.status}): ${error.message || error.statusText}\n\nCheck the service logs for more details.`;
    } else {
      errorMessage = `Request failed (${error.status}): ${error.message || error.statusText || 'Unknown error'}`;
    }

    alert(`Operation Failed:\n\n${errorMessage}`);
  }
}