import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ValidationService } from '../validation/validation.service';
import { ErrorHandlerService } from '../error/error-handler.service';

export interface CrawlerRequest {
  url: string;
  priority?: string;
  maxDepth?: string;
}

export interface CrawlerResponse {
  message: string;
  url: string;
  priority: string;
  maxDepth: string;
}

@Injectable({
  providedIn: 'root'
})
export class CrawlerService extends BaseService {

  constructor(
    http: HttpClient,
    private validationService: ValidationService,
    private errorHandler: ErrorHandlerService
  ) {
    super(http);
  }

  submitUrl(url: string, priority: string = '5', maxDepth: string = '3'): Observable<CrawlerResponse> {
    // Validate URL
    const validation = this.validationService.validateUrl(url);
    if (!validation.isValid) {
      throw new Error(validation.errors.join(', '));
    }

    // Validate priority (1-10)
    const priorityNum = parseInt(priority);
    if (isNaN(priorityNum) || priorityNum < 1 || priorityNum > 10) {
      throw new Error('Priority must be a number between 1 and 10');
    }

    // Validate maxDepth (1-10)
    const depthNum = parseInt(maxDepth);
    if (isNaN(depthNum) || depthNum < 1 || depthNum > 10) {
      throw new Error('Max depth must be a number between 1 and 10');
    }

    const request: CrawlerRequest = {
      url: url.trim(),
      priority: priority,
      maxDepth: maxDepth
    };

    return this.post<CrawlerResponse>('crawler-service/crawler', request)
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'CrawlerService.submitUrl');
          throw error;
        })
      );
  }

  getCrawlerStatus(): Observable<any> {
    return this.get<any>('crawler-service/status')
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'CrawlerService.getCrawlerStatus');
          throw error;
        })
      );
  }

  getCrawlerMetrics(): Observable<any> {
    return this.get<any>('crawler-service/metrics')
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'CrawlerService.getCrawlerMetrics');
          throw error;
        })
      );
  }
}
