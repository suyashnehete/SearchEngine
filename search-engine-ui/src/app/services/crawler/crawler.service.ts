import {Injectable} from '@angular/core';
import {BaseService} from '../base-service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CrawlerService extends BaseService {

  submitUrl(url: string): Observable<string> {
    return this.post<string>(`crawler`, {url: url});
  }

}
