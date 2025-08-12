import { Injectable } from '@angular/core';
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
    private validationService: ValidationService,
    private errorHandler: ErrorHandlerService
  ) {
    super();
  }

  logUserQuery(userId: string, query: string): Observable<any> {
    // Validate inputs
    const userValidation = this.validationService.validateUserId(userId);
    const queryValidation = this.validationService.validateSearchQuery(query);

    if (!userValidation.isValid || !queryValidation.isValid) {
      // Don't log invalid queries, but don't throw error either
      return of(null);
    }

    const logRequest: LogRequest = {
      userId: userId.trim(),
      query: query.trim()
    };

    return this.post('log/log-query', logRequest)
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'LoggingService.logUserQuery');
          // Don't propagate logging errors to the user
          return of(null);
        })
      );
  }

  getQueryLogs(userId: string): Observable<any[]> {
    const userValidation = this.validationService.validateUserId(userId);
    if (!userValidation.isValid) {
      return of([]);
    }

    return this.get<any[]>(`log/user/${userId}`)
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'LoggingService.getQueryLogs');
          return of([]);
        })
      );
  }
}
