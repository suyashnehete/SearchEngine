import {Injectable} from '@angular/core';
import {BaseService} from '../base-service';

@Injectable({
  providedIn: 'root'
})
export class LoggingService extends BaseService {

  logUserQuery(userId: string, query: string) {
    return this.post('log/log-query', {userId, query});
  }

}
