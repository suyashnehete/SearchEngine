import { Component } from '@angular/core';
import { SearchService } from 'src/app/services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {
  query: string = '';
  results: number[] = [];
  isLoading: boolean = false;

  constructor(private searchService: SearchService) {}

  onSearch() {
    if (!this.query.trim()) return;

    this.isLoading = true;
    this.searchService.search(this.query).subscribe({
      next: (data) => {
        this.results = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching results:', err);
        this.isLoading = false;
      },
    });
  }
}
