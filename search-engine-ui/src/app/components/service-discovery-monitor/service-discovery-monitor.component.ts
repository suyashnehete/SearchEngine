import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subscription } from 'rxjs';
import { DiscoveryService, ServiceDiscoveryStatus, EurekaApplication } from '../../services/discovery/discovery.service';

@Component({
  selector: 'app-service-discovery-monitor',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './service-discovery-monitor.component.html',
  styleUrls: ['./service-discovery-monitor.component.scss']
})
export class ServiceDiscoveryMonitorComponent implements OnInit, OnDestroy {
  discoveryStatus: ServiceDiscoveryStatus | null = null;
  communicationResults: { [serviceName: string]: boolean } | null = null;
  gatewayRoutes: any[] = [];
  isLoading = false;
  isTestingCommunication = false;
  private refreshSubscription?: Subscription;

  constructor(private discoveryService: DiscoveryService) { }

  ngOnInit() {
    this.refreshStatus();
    this.loadGatewayRoutes();
    // Auto-refresh every 30 seconds
    this.refreshSubscription = interval(30000).subscribe(() => {
      if (!this.isLoading) {
        this.refreshStatus();
      }
    });
  }

  ngOnDestroy() {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  refreshStatus() {
    this.isLoading = true;
    this.discoveryService.getServiceDiscoveryStatus().subscribe({
      next: (status) => {
        this.discoveryStatus = status;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to get discovery status:', error);
        this.isLoading = false;
      }
    });
  }

  testCommunication() {
    this.isTestingCommunication = true;
    this.discoveryService.testServiceCommunication().subscribe({
      next: (results) => {
        this.communicationResults = results;
        this.isTestingCommunication = false;
      },
      error: (error) => {
        console.error('Failed to test communication:', error);
        this.isTestingCommunication = false;
      }
    });
  }

  loadGatewayRoutes() {
    this.discoveryService.getServiceRoutes().subscribe({
      next: (routes) => {
        this.gatewayRoutes = routes;
      },
      error: (error) => {
        console.error('Failed to load gateway routes:', error);
      }
    });
  }

  getCommunicationResults(): Array<{ name: string, success: boolean }> {
    if (!this.communicationResults) return [];

    return Object.keys(this.communicationResults).map(name => ({
      name,
      success: this.communicationResults![name] ?? false
    }));
  }

  formatTimestamp(timestamp: number): string {
    return new Date(timestamp).toLocaleString();
  }
}