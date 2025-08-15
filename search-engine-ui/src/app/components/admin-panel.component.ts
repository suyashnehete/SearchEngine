import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-panel',
  template: `
    <div class="container mt-4">
      <div class="row">
        <div class="col-12">
          <h2><i class="fas fa-cogs me-2"></i>Admin Panel</h2>
          <p class="text-muted">Manage indexing operations</p>
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
              <button class="btn btn-info" (click)="viewCrawlerQueue()">
                <i class="fas fa-list me-1"></i>
                View Queue
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
export class AdminPanelComponent {

  constructor(private http: HttpClient) { }

  startCrawler() {
    this.http.post('/api/crawler-service/admin/start', {}).subscribe({
      next: () => console.log('Crawler started'),
      error: (err) => console.error('Error starting crawler:', err)
    });
  }

  stopCrawler() {
    this.http.post('/api/crawler-service/admin/stop', {}).subscribe({
      next: () => console.log('Crawler stopped'),
      error: (err) => console.error('Error stopping crawler:', err)
    });
  }

  reindexAll() {
    this.http.post('/api/indexer-service/admin/reindex', {}).subscribe({
      next: () => console.log('Reindexing started'),
      error: (err) => console.error('Error starting reindex:', err)
    });
  }

  clearIndex() {
    if (confirm('Are you sure you want to clear the entire index? This action cannot be undone.')) {
      this.http.delete('/api/indexer-service/admin/index').subscribe({
        next: () => console.log('Index cleared'),
        error: (err) => console.error('Error clearing index:', err)
      });
    }
  }

  optimizeIndex() {
    this.http.post('/api/indexer-service/admin/optimize', {}).subscribe({
      next: () => console.log('Index optimization started'),
      error: (err) => console.error('Error optimizing index:', err)
    });
  }

  viewCrawlerQueue() {
    this.http.get('/api/crawler-service/admin/queue').subscribe({
      next: (data) => console.log('Crawler queue:', data),
      error: (err) => console.error('Error fetching queue:', err)
    });
  }

  viewBlockedDomains() {
    // This would be implemented later
    console.log('Blocked domains feature coming soon');
  }

  viewCrawlPolicies() {
    // This would be implemented later
    console.log('Crawl policies feature coming soon');
  }

  viewSystemMetrics() {
    this.http.get('/api/crawler-service/admin/stats').subscribe({
      next: (data) => console.log('Crawler stats:', data),
      error: (err) => console.error('Error fetching crawler stats:', err)
    });

    this.http.get('/api/indexer-service/admin/stats').subscribe({
      next: (data) => console.log('Index stats:', data),
      error: (err) => console.error('Error fetching index stats:', err)
    });
  }

  viewSearchAnalytics() {
    // This would be implemented later with query service analytics
    console.log('Search analytics feature coming soon');
  }
}