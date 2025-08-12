import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { LoggingService } from 'src/app/services/logging/logging.service';
import { SearchResponse } from 'src/app/services/models/search-response';
import { SearchService } from 'src/app/services/search/search.service';
import { LoadingService } from 'src/app/services/loading/loading.service';
import { ErrorHandlerService, UserFriendlyError } from 'src/app/services/error/error-handler.service';
import { ValidationService } from 'src/app/services/validation/validation.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, OnDestroy {

  query: string = '';
  searchResponse: SearchResponse | undefined;
  suggestions: string[] = [];
  page = 1;
  size = 10;
  userId: string = 'user_' + Math.random().toString(36).substr(2, 9); // Generate unique user ID

  // UI State
  isSearchLoading = false;
  isSuggestionsLoading = false;
  errorMessage: string | null = null;
  validationErrors: string[] = [];

  // Debouncing
  private searchSubject = new Subject<string>();
  private subscriptions: Subscription[] = [];

  constructor(
    private searchService: SearchService,
    private loggingService: LoggingService,
    private loadingService: LoadingService,
    private errorHandler: ErrorHandlerService,
    private validationService: ValidationService
  ) { }

  ngOnInit(): void {
    // Set up debounced suggestions
    const suggestionsSub = this.searchSubject.pipe(
      debounceTime(environment.debounceTime),
      distinctUntilChanged()
    ).subscribe(query => {
      this.getSuggestions(query);
    });

    this.subscriptions.push(suggestionsSub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onSearch(): void {
    this.page = 1;
    this.suggestions = [];
    this.clearErrors();
    this.search();
  }

  search(): void {
    // Validate query
    const validation = this.validationService.validateSearchQuery(this.query);
    if (!validation.isValid) {
      this.validationErrors = validation.errors;
      return;
    }

    this.isSearchLoading = true;
    this.loadingService.setSearchLoading(true);
    this.clearErrors();

    // Log user query (don't wait for response)
    this.logUserQuery();

    this.searchService.search(this.query, 50, this.page, this.size).subscribe({
      next: (data) => {
        this.searchResponse = data;
        this.isSearchLoading = false;
        this.loadingService.setSearchLoading(false);
      },
      error: (err) => {
        this.handleSearchError(err);
        this.isSearchLoading = false;
        this.loadingService.setSearchLoading(false);
      }
    });
  }

  private logUserQuery(): void {
    this.loggingService.logUserQuery(this.userId, this.query).subscribe({
      next: () => {
        // Logging successful - no user feedback needed
      },
      error: (err) => {
        // Log error but don't show to user
        this.errorHandler.logError(err, 'Failed to log user query');
      }
    });
  }

  onInputChange(): void {
    this.clearErrors();

    if (!this.query.trim()) {
      this.suggestions = [];
      return;
    }

    // Use debounced subject for suggestions
    this.searchSubject.next(this.query);
  }

  private getSuggestions(query: string): void {
    if (!query.trim()) {
      this.suggestions = [];
      return;
    }

    this.isSuggestionsLoading = true;
    this.loadingService.setSuggestionsLoading(true);

    this.searchService.getSuggestions(query, this.userId).subscribe({
      next: (data) => {
        this.suggestions = data || [];
        this.isSuggestionsLoading = false;
        this.loadingService.setSuggestionsLoading(false);
      },
      error: (err) => {
        this.suggestions = [];
        this.isSuggestionsLoading = false;
        this.loadingService.setSuggestionsLoading(false);
        // Don't show suggestions errors to user
        this.errorHandler.logError(err, 'Failed to fetch suggestions');
      }
    });
  }

  setQuery(query: string): void {
    this.suggestions = [];
    this.query = query;
    this.search();
  }

  // Pagination methods with validation
  goToNextPage(): void {
    if (this.searchResponse && this.page < this.searchResponse.totalPages) {
      this.page++;
      this.search();
    }
  }

  goToPage(pageNumber: number): void {
    if (this.searchResponse && pageNumber >= 1 && pageNumber <= this.searchResponse.totalPages) {
      this.page = pageNumber;
      this.search();
    }
  }

  goToLastPage(): void {
    if (this.searchResponse) {
      this.page = this.searchResponse.totalPages;
      this.search();
    }
  }

  goToPreviousPage(): void {
    if (this.page > 1) {
      this.page--;
      this.search();
    }
  }

  goToFirstPage(): void {
    this.page = 1;
    this.search();
  }

  // Error handling
  private handleSearchError(error: any): void {
    const userError = this.errorHandler.handleApiError(error);
    this.errorMessage = userError.message;
    this.searchResponse = undefined;
  }

  private clearErrors(): void {
    this.errorMessage = null;
    this.validationErrors = [];
  }

  // Retry search
  retrySearch(): void {
    this.clearErrors();
    this.search();
  }

  // Check if pagination should be shown
  shouldShowPagination(): boolean {
    return !!(this.searchResponse?.documents?.length &&
      this.searchResponse.totalPages > 1 &&
      this.query.trim());
  }

  // Get page numbers for pagination
  getPageNumbers(): number[] {
    if (!this.searchResponse) return [];

    const totalPages = this.searchResponse.totalPages;
    const currentPage = this.searchResponse.currentPage;
    const maxPagesToShow = 5;

    let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    // Adjust start page if we're near the end
    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    const pages: number[] = [];
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }
}
