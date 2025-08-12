import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError, timer } from "rxjs";
import { catchError, retry, timeout, retryWhen, concatMap, finalize } from "rxjs/operators";
import { environment } from "../../environments/environment";

export interface ApiError {
  message: string;
  status: number;
  timestamp: number;
  path?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BaseService {
  private readonly apiUrl = environment.apiUrl;
  private readonly retryAttempts = environment.retryAttempts;
  private readonly retryDelay = environment.retryDelay;
  private readonly requestTimeout = environment.requestTimeout;

  private readonly httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  get<T>(path: string, options?: any): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}/${path}`, { ...this.httpOptions, ...options })
      .pipe(
        timeout(this.requestTimeout),
        retryWhen(errors => this.retryStrategy(errors)),
        catchError(this.handleError.bind(this))
      );
  }

  post<T>(path: string, body: any, options?: any): Observable<T> {
    const sanitizedBody = this.sanitizeInput(body);
    return this.http.post<T>(`${this.apiUrl}/${path}`, sanitizedBody, { ...this.httpOptions, ...options })
      .pipe(
        timeout(this.requestTimeout),
        retryWhen(errors => this.retryStrategy(errors)),
        catchError(this.handleError.bind(this))
      );
  }

  put<T>(path: string, body: any, options?: any): Observable<T> {
    const sanitizedBody = this.sanitizeInput(body);
    return this.http.put<T>(`${this.apiUrl}/${path}`, sanitizedBody, { ...this.httpOptions, ...options })
      .pipe(
        timeout(this.requestTimeout),
        retryWhen(errors => this.retryStrategy(errors)),
        catchError(this.handleError.bind(this))
      );
  }

  delete<T>(path: string, options?: any): Observable<T> {
    return this.http.delete<T>(`${this.apiUrl}/${path}`, { ...this.httpOptions, ...options })
      .pipe(
        timeout(this.requestTimeout),
        retryWhen(errors => this.retryStrategy(errors)),
        catchError(this.handleError.bind(this))
      );
  }

  private retryStrategy(errors: Observable<any>): Observable<any> {
    return errors.pipe(
      concatMap((error, index) => {
        if (index >= this.retryAttempts) {
          return throwError(error);
        }

        // Only retry on network errors or 5xx server errors
        if (this.shouldRetry(error)) {
          console.warn(`Retrying request (attempt ${index + 1}/${this.retryAttempts}):`, error.message);
          return timer(this.retryDelay * Math.pow(2, index)); // Exponential backoff
        }

        return throwError(error);
      })
    );
  }

  private shouldRetry(error: HttpErrorResponse): boolean {
    // Retry on network errors or server errors (5xx)
    return !error.status || error.status >= 500;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let apiError: ApiError;

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      apiError = {
        message: `Network error: ${error.error.message}`,
        status: 0,
        timestamp: Date.now()
      };
    } else {
      // Server-side error
      apiError = {
        message: error.error?.message || error.message || 'An unexpected error occurred',
        status: error.status || 500,
        timestamp: Date.now(),
        path: error.url || undefined
      };
    }

    console.error('API Error:', apiError);
    return throwError(apiError);
  }

  private sanitizeInput(input: any): any {
    if (typeof input === 'string') {
      return input.trim().replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
    }

    if (typeof input === 'object' && input !== null) {
      const sanitized: any = {};
      for (const key in input) {
        if (input.hasOwnProperty(key)) {
          sanitized[key] = this.sanitizeInput(input[key]);
        }
      }
      return sanitized;
    }

    return input;
  }
}
