import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, Subscription } from 'rxjs';
import { HealthService, SystemHealth, ServiceHealth } from '../../services/health/health.service';

@Component({
    selector: 'app-health-monitor',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './health-monitor.component.html',
    styleUrls: ['./health-monitor.component.scss']
})
export class HealthMonitorComponent implements OnInit, OnDestroy {
    systemHealth: SystemHealth | null = null;
    isLoading = false;
    private refreshSubscription?: Subscription;

    constructor(private healthService: HealthService) { }

    ngOnInit() {
        this.refreshHealth();
        // Auto-refresh every 30 seconds
        this.refreshSubscription = interval(30000).subscribe(() => {
            if (!this.isLoading) {
                this.refreshHealth();
            }
        });
    }

    ngOnDestroy() {
        if (this.refreshSubscription) {
            this.refreshSubscription.unsubscribe();
        }
    }

    refreshHealth() {
        this.isLoading = true;
        this.healthService.checkSystemHealth().subscribe({
            next: (health) => {
                this.systemHealth = health;
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Failed to check system health:', error);
                this.isLoading = false;
            }
        });
    }

    getComponents(components: any): Array<{ name: string, status: string }> {
        if (!components) return [];

        return Object.keys(components).map(key => ({
            name: key,
            status: components[key].status || 'UNKNOWN'
        }));
    }

    formatTimestamp(timestamp: number): string {
        return new Date(timestamp).toLocaleString();
    }
}