import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private apiUrl = 'http://localhost:8080/api/v1/search';

  constructor(
    private http: HttpClient
  ) { }

  search(query: string, topK: number = 10): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}?query=${query}&topK=${topK}`);
  }
}
