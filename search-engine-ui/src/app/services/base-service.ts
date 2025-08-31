import { HttpClient, HttpErrorResponse, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError, timer } from "rxjs";
import { catchError, timeout, retry } from "rxjs/operators";
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
    const requestOptions = {
      headers: this.httpOptions.headers,
      ...(options || {})
    };

    return (this.http.get(`${this.apiUrl}/${path}`, requestOptions) as Observable<T>)
      .pipe(
        timeout(this.requestTimeout),
        retry(this.retryAttempts),
        catchError(this.handleError.bind(this))
      );
  }

  post<T>(path: string, body: any, options?: any): Observable<T> {
    const sanitizedBody = this.sanitizeInput(body);
    const requestOptions = {
      headers: this.httpOptions.headers,
      ...(options || {})
    };

    return (this.http.post(`${this.apiUrl}/${path}`, sanitizedBody, requestOptions) as Observable<T>)
      .pipe(
        timeout(this.requestTimeout),
        retry(this.retryAttempts),
        catchError(this.handleError.bind(this))
      );
  }

  put<T>(path: string, body: any, options?: any): Observable<T> {
    const sanitizedBody = this.sanitizeInput(body);
    const requestOptions = {
      headers: this.httpOptions.headers,
      ...(options || {})
    };

    return (this.http.put(`${this.apiUrl}/${path}`, sanitizedBody, requestOptions) as Observable<T>)
      .pipe(
        timeout(this.requestTimeout),
        retry(this.retryAttempts),
        catchError(this.handleError.bind(this))
      );
  }

  delete<T>(path: string, options?: any): Observable<T> {
    const requestOptions = {
      headers: this.httpOptions.headers,
      ...(options || {})
    };

    return (this.http.delete(`${this.apiUrl}/${path}`, requestOptions) as Observable<T>)
      .pipe(
        timeout(this.requestTimeout),
        retry(this.retryAttempts),
        catchError(this.handleError.bind(this))
      );
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
    return throwError(() => apiError);
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