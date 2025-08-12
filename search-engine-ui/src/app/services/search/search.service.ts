import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { SearchResponse } from '../models/search-response';
import { BaseService } from '../base-service';
import { ValidationService } from '../validation/validation.service';
import { CacheService } from '../cache/cache.service';
import { ErrorHandlerService } from '../error/error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class SearchService extends BaseService {

  constructor(
    private validationService: ValidationService,
    private cacheService: CacheService,
    private errorHandler: ErrorHandlerService
  ) {
    super();
  }

  search(query: string, topK: number = 50, page: number = 1, size: number = 10): Observable<SearchResponse> {
    // Validate input
    const queryValidation = this.validationService.validateSearchQuery(query);
    if (!queryValidation.isValid) {
      throw new Error(queryValidation.errors.join(', '));
    }

    const paginationValidation = this.validationService.validatePagination(page, size);
    if (!paginationValidation.isValid) {
      throw new Error(paginationValidation.errors.join(', '));
    }

    // Check cache first
    const cacheKey = this.cacheService.generateSearchKey(query, page, size, topK);
    const cachedResult = this.cacheService.get<SearchResponse>(cacheKey);

    if (cachedResult) {
      return of(cachedResult);
    }

    // Make API call
    const params = new URLSearchParams({
      query: query.trim(),
      topK: topK.toString(),
      page: page.toString(),
      size: size.toString()
    });

    return this.get<SearchResponse>(`search?${params.toString()}`)
      .pipe(
        tap(response => {
          // Cache successful response
          this.cacheService.set(cacheKey, response);
        }),
        catchError(error => {
          this.errorHandler.logError(error, 'SearchService.search');
          throw error;
        })
      );
  }

  getSuggestions(prefix: string, userId: string): Observable<string[]> {
    // Validate input
    const queryValidation = this.validationService.validateSearchQuery(prefix);
    if (!queryValidation.isValid) {
      return of([]);
    }

    const userValidation = this.validationService.validateUserId(userId);
    if (!userValidation.isValid) {
      return of([]);
    }

    // Check cache first
    const cacheKey = this.cacheService.generateSuggestionsKey(prefix, userId);
    const cachedResult = this.cacheService.get<string[]>(cacheKey);

    if (cachedResult) {
      return of(cachedResult);
    }

    // Make API call
    const params = new URLSearchParams({
      prefix: prefix.trim(),
      userId: userId.trim()
    });

    return this.get<string[]>(`suggestions?${params.toString()}`)
      .pipe(
        tap(suggestions => {
          // Cache successful response with shorter TTL for suggestions
          this.cacheService.set(cacheKey, suggestions, 60000); // 1 minute
        }),
        catchError(error => {
          this.errorHandler.logError(error, 'SearchService.getSuggestions');
          return of([]); // Return empty array on error
        })
      );
  }

  // Advanced search with filters
  searchWithFilters(query: string, filters: any, topK: number = 50, page: number = 1, size: number = 10): Observable<SearchResponse> {
    const queryValidation = this.validationService.validateSearchQuery(query);
    if (!queryValidation.isValid) {
      throw new Error(queryValidation.errors.join(', '));
    }

    const params = new URLSearchParams({
      query: query.trim(),
      topK: topK.toString(),
      page: page.toString(),
      size: size.toString()
    });

    // Add filters to params
    if (filters.tags && filters.tags.length > 0) {
      params.append('tags', filters.tags.join(','));
    }

    return this.get<SearchResponse>(`search/filters?${params.toString()}`)
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'SearchService.searchWithFilters');
          throw error;
        })
      );
  }

  // Search with corrections
  searchWithCorrections(query: string, topK: number = 50, page: number = 1, size: number = 10): Observable<SearchResponse> {
    const queryValidation = this.validationService.validateSearchQuery(query);
    if (!queryValidation.isValid) {
      throw new Error(queryValidation.errors.join(', '));
    }

    const params = new URLSearchParams({
      query: query.trim(),
      topK: topK.toString(),
      page: page.toString(),
      size: size.toString()
    });

    return this.get<SearchResponse>(`search/corrections?${params.toString()}`)
      .pipe(
        catchError(error => {
          this.errorHandler.logError(error, 'SearchService.searchWithCorrections');
          throw error;
        })
      );
  }
}
