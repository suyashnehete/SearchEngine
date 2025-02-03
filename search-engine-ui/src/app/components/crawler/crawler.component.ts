import {Component} from '@angular/core';
import {CrawlerService} from 'src/app/services/crawler/crawler.service';

@Component({
  selector: 'app-crawler',
  templateUrl: './crawler.component.html',
  styleUrls: ['./crawler.component.scss']
})
export class CrawlerComponent {
  crawlUrl: string = '';

  constructor(
    private crawlerService: CrawlerService,
  ) {
  }

  onSubmitUrl() {
    if (!this.crawlUrl.trim()) return;

    this.crawlerService
      .submitUrl(this.crawlUrl)
      .subscribe({
        next: () => {
          alert('URL submitted successfully');
        },
        error: (err) => {
          console.error('Error submitting URL:', err);
          alert('Failed to submit URL');
        },
      });
  }

}
