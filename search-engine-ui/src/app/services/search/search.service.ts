import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {SearchResponse} from '../models/search-response';
import {BaseService} from '../base-service';

@Injectable({
  providedIn: 'root'
})
export class SearchService extends BaseService {

  search(query: string, topK: number = 10, page: number, size: number): Observable<SearchResponse> {
    return this.get<SearchResponse>(`search?query=${query}&topK=${topK}&page=${page}&size=${size}`);
  }

  getSuggestions(query: string, userId: string): Observable<string[]> {
    return this.get<string[]>(`suggestions?prefix=${query}&userId=${userId}`);
  }

}
