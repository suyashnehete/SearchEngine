import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SearchComponent} from './components/search/search.component';
import {CrawlerComponent} from './components/crawler/crawler.component';

const routes: Routes = [
  {
    path: '',
    component: SearchComponent
  },
  {
    path: 'crawl',
    component: CrawlerComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
