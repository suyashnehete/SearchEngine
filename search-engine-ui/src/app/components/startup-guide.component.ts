import { Component } from '@angular/core';

@Component({
    selector: 'app-startup-guide',
    template: `
    <div class="container mt-4">
      <div class="alert alert-warning">
        <h4><i class="fas fa-exclamation-triangle me-2"></i>Backend Services Required</h4>
        <p>To use admin features, you need to start the backend microservices. Follow these steps:</p>
        
        <div class="row mt-3">
          <div class="col-md-6">
            <h6>1. Start Infrastructure (if not running)</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>docker-compose up -d</code>
            </div>
          </div>
          <div class="col-md-6">
            <h6>2. Start Discovery Service</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>cd search_engine_microservice/discovery<br>mvn spring-boot:run</code>
            </div>
          </div>
        </div>
        
        <div class="row mt-3">
          <div class="col-md-6">
            <h6>3. Start Config Server</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>cd search_engine_microservice/config-server<br>mvn spring-boot:run</code>
            </div>
          </div>
          <div class="col-md-6">
            <h6>4. Start Auth Server</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>cd search_engine_microservice/auth-server<br>mvn spring-boot:run</code>
            </div>
          </div>
        </div>
        
        <div class="row mt-3">
          <div class="col-md-6">
            <h6>5. Start Gateway</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>cd search_engine_microservice/gateway<br>mvn spring-boot:run</code>
            </div>
          </div>
          <div class="col-md-6">
            <h6>6. Start Business Services</h6>
            <div class="bg-dark text-light p-2 rounded">
              <code>cd search_engine_microservice/crawler<br>mvn spring-boot:run</code>
            </div>
          </div>
        </div>
        
        <div class="mt-3">
          <h6>Service Ports:</h6>
          <ul class="list-unstyled">
            <li><strong>Discovery:</strong> 8761 - <a href="http://localhost:8761" target="_blank">http://localhost:8761</a></li>
            <li><strong>Gateway:</strong> 8081 - Main API endpoint</li>
            <li><strong>Auth Server:</strong> 8080 - Authentication</li>
            <li><strong>Crawler:</strong> 8082 - Web crawling</li>
            <li><strong>Indexer:</strong> 8083 - Content indexing</li>
            <li><strong>Query:</strong> 8084 - Search processing</li>
          </ul>
        </div>
        
        <div class="mt-3">
          <small class="text-muted">
            <i class="fas fa-info-circle me-1"></i>
            Start services in the order shown above for proper dependency resolution.
            Visit the Discovery service at <a href="http://localhost:8761" target="_blank">http://localhost:8761</a> to verify all services are registered.
          </small>
        </div>
      </div>
    </div>
  `
})
export class StartupGuideComponent { }