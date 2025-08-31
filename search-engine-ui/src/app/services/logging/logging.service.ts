import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ValidationService } from '../validation/validation.service';
import { ErrorHandlerService } from '../error/error-handler.service';

export interface LogRequest {
  userId: string;
  query: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoggingService extends BaseService {

  constructor(
    http: HttpClient,
    private validationService: ValidationService,
    private errorHandler: ErrorHandlerService
  ) {
    super(http);
  }

  logUserQuery(userId: string, query: string): Observable<any> {
    // Logging is disabled - no backend logging service configured
    // Just log to console for debugging and return success
    console.log('Search query logged:', {
      userId: userId || 'anonymous',
      query: query.trim(),
      timestamp: new Date().toISOString()
    });

    // Return successful response without making API call
    return of({ success: true, message: 'Logged locally' });
  }

  getQueryLogs(userId: string): Observable<any[]> {
    // Logging service is disabled - return empty array
    console.log('Query logs requested for user:', userId || 'anonymous');
    return of([]);
  }
}
