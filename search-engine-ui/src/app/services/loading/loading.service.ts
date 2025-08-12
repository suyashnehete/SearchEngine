import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LoadingService {
    private loadingSubject = new BehaviorSubject<boolean>(false);
    private loadingStates = new Map<string, boolean>();

    // Global loading state
    loading$: Observable<boolean> = this.loadingSubject.asObservable();

    setLoading(loading: boolean, key?: string): void {
        if (key) {
            // Set loading state for specific operation
            this.loadingStates.set(key, loading);

            // Update global loading state
            const hasAnyLoading = Array.from(this.loadingStates.values()).some(state => state);
            this.loadingSubject.next(hasAnyLoading);
        } else {
            // Set global loading state
            this.loadingSubject.next(loading);
        }
    }

    isLoading(key?: string): boolean {
        if (key) {
            return this.loadingStates.get(key) || false;
        }
        return this.loadingSubject.value;
    }

    // Specific loading states for different operations
    setSearchLoading(loading: boolean): void {
        this.setLoading(loading, 'search');
    }

    setSuggestionsLoading(loading: boolean): void {
        this.setLoading(loading, 'suggestions');
    }

    setCrawlerLoading(loading: boolean): void {
        this.setLoading(loading, 'crawler');
    }

    isSearchLoading(): boolean {
        return this.isLoading('search');
    }

    isSuggestionsLoading(): boolean {
        return this.isLoading('suggestions');
    }

    isCrawlerLoading(): boolean {
        return this.isLoading('crawler');
    }
}