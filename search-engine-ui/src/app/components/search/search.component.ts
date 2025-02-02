import { Component } from '@angular/core';
import { SearchResponse } from 'src/app/services/models/search-response';
import { SearchService } from 'src/app/services/search/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {

  query: string = '';
  searchResponse: SearchResponse | undefined;
  page = 1;
  size = 10;

  constructor(private searchService: SearchService) { }

  onSearch() {
    this.page = 1;
    this.search();
  }

  search() {
    if (!this.query.trim()) return;

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
