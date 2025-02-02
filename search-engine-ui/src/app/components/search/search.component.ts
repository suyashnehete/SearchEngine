import { Component } from '@angular/core';
import { LoggingService } from 'src/app/services/logging/logging.service';
import { SearchResponse } from 'src/app/services/models/search-response';
import { UrlResponse } from 'src/app/services/models/url-response';
import { SearchService } from 'src/app/services/search/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {

  query: string = '';
  searchResponse: SearchResponse | undefined;
  suggestions: string[] = [];
  page = 1;
  size = 10;
  userId: string = 'anonymous'; // Hardcoded user ID for now

  constructor(
    private searchService: SearchService,
    private loggingService: LoggingService,
  ) { }

  onSearch() {
    this.page = 1;
    this.search();
  }

  search() {
    if (!this.query.trim()) return;

    this.logUserQuery();

    this.searchService.search(this.query, 10, this.page, this.size).subscribe({
      next: (data) => {
        this.searchResponse = data;
      },
      error: (err) => {
        this.searchResponse = undefined;
        console.error('Error fetching results:', err);
      },
    });
  }

  logUserQuery() {
    // Log the query before searching
    this.loggingService.logUserQuery(this.userId, this.query).subscribe({
      next: () => {
        console.log('Query logged successfully');
      },
      error: (err) => {
        console.error('Error logging query:', err);
      },
    });
  }

  onInputChange() {
    if (!this.query.trim()) {
      this.suggestions = [];
      return;
    }

    this.searchService
      .getSuggestions(this.query)
      .subscribe({
        next: (data) => {
          this.suggestions = data;
        },
        error: (err) => {
          this.suggestions = [];
          console.error('Error fetching suggestions:', err);
        },
      });
  }

  setQuery(query: string) {
    this.query = query;
    this.search();
  }

  goToNextPage() {
    this.page++;
    this.search();
  }
  goToPage(i: any) {
    this.page = i;
    this.search();
  }
  goToLastPage() {
    this.page = this.searchResponse?.totalPages as number;
    this.search();
  }
  goToPreviousPage() {
    this.page--;
    this.search();
  }
  goToFirstPage() {
    this.page = 1;
    this.search();
  }

}
