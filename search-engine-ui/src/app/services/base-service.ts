import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class BaseService {

  private apiUrl = 'http://localhost:8222/api/v1/'; // microservice url
  //private apiUrl = 'http://localhost:8080/api/v1/'; // monolith url

  constructor(
    private http: HttpClient
  ) {
  }

  get<T>(path: string): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}${path}`);
  }

  post<T>(path: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}${path}`, body);
  }

}
