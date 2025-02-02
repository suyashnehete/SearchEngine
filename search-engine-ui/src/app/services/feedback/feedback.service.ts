import { Injectable } from '@angular/core';
import { BaseService } from '../base-service';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService extends BaseService {

  submitFeedback(userId: string, query: string, documentId: number, isRelevant: boolean) {
    return this.post('feedback', { userId, query, documentId, isRelevant });
  }
}
