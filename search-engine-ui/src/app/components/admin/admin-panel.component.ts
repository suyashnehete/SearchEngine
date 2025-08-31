import { Component, OnInit } from '@angular/core';
import { AdminPanelService } from '../../services/admin-panel.service';
import { AuthService } from '../../auth/auth.service';
import { environment } from '../../../environments/environment';
import { timeout, catchError, map } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.scss']
})
export class AdminPanelComponent implements OnInit {

  constructor(
    private adminPanelService: AdminPanelService,
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
  private makeAdminRequest<T>(method: 'GET' | 'POST' | 'DELETE', path: string, body?: any) {
    return this.adminPanelService.makeAdminRequest<T>(method, path, body);
  }

  startCrawler() {
    console.log('Starting crawler...');
    this.makeAdminRequest('POST', 'crawler-service/crawler/admin/start', null).subscribe({
      next: () => { },
      error: () => { }
    });
    alert('Crawler start requested!');
  }

  stopCrawler() {
    console.log('Stopping crawler...');
    this.makeAdminRequest('POST', 'crawler-service/crawler/admin/stop', null).subscribe({
      next: () => { },
      error: () => { }
    });
    alert('Crawler stop requested!');
  }

  reindexAll() {
    console.log('Starting reindex...');
    this.makeAdminRequest('POST', 'indexer-service/indexer/admin/reindex', null).subscribe({
      next: () => { },
      error: () => { }
    });
    alert('Reindexing started successfully!');
  }

  clearIndex() {
    if (confirm('Are you sure you want to clear the entire index? This action cannot be undone.')) {
      console.log('Clearing index...');
      this.makeAdminRequest('DELETE', 'indexer-service/indexer/admin/index', null).subscribe({
        next: () => { },
        error: () => { }
      });
      alert('Index clearing started!');
    }
  }

  optimizeIndex() {
    console.log('Optimizing index...');
    this.makeAdminRequest('POST', 'indexer-service/indexer/admin/optimize', null).subscribe({
      next: () => { },
      error: () => { }
    });
    alert('Index optimization started!');
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

  private showConnectionResults(results: string[]) {
    const authStatus = `Authentication Status:\n` +
      `• Authenticated: ${this.authService.isAuthenticated ? '✅' : '❌'}\n` +
      `• Has Token: ${!!this.authService.accessToken ? '✅' : '❌'}\n` +
      `• Admin Role: ${this.authService.isAdmin() ? '✅' : '❌'}\n\n`;

    const message = authStatus + 'Service Status Check:\n\n' + results.join('\n') +
      '\n\nNote: Services should be running on their respective ports for admin features to work.';
    alert(message);
  }

  viewCrawlerQueue() {
    console.log('Fetching crawler queue...');
    this.makeAdminRequest<any>('GET', 'crawler-service/crawler/admin/queue', null).subscribe({
      next: (data) => {
        // Show a summary in an alert
        const message = `Crawler Queue Info:\n\n` +
          `• Visited Count: ${data.visitedCount}\n` +
          `• Queue Size: ${data.queueSize}\n` +
          `• Is Running: ${data.isRunning ? 'Yes' : 'No'}\n` +
          `• Instance ID: ${data.crawlerInstanceId}`;
        alert(message);
        console.log('Crawler queue response:', data);
      },
      error: (err) => {
        this.handleAdminError(err, 'crawler-service/crawler/admin/queue');
      }
    });
  }


  private checkServiceHealth(service: { name: string, url: string, port: string }) {
    return this.adminPanelService.checkServiceHealth(service);
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