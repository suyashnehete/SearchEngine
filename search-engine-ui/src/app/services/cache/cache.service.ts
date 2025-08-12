import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

interface CacheItem<T> {
    data: T;
    timestamp: number;
    ttl: number;
}

@Injectable({
    providedIn: 'root'
})
export class CacheService {
    private cache = new Map<string, CacheItem<any>>();
    private readonly defaultTtl = environment.cacheTimeout;

    set<T>(key: string, data: T, ttl: number = this.defaultTtl): void {
        const item: CacheItem<T> = {
            data,
            timestamp: Date.now(),
            ttl
        };

        this.cache.set(key, item);

        // Set cleanup timer
        setTimeout(() => {
            this.delete(key);
        }, ttl);
    }

    get<T>(key: string): T | null {
        const item = this.cache.get(key);

        if (!item) {
            return null;
        }

        // Check if item has expired
        if (Date.now() - item.timestamp > item.ttl) {
            this.delete(key);
            return null;
        }

        return item.data as T;
    }

    has(key: string): boolean {
        const item = this.cache.get(key);

        if (!item) {
            return false;
        }

        // Check if item has expired
        if (Date.now() - item.timestamp > item.ttl) {
            this.delete(key);
            return false;
        }

        return true;
    }

    delete(key: string): boolean {
        return this.cache.delete(key);
    }

    clear(): void {
        this.cache.clear();
    }

    size(): number {
        return this.cache.size;
    }

    // Generate cache key for search queries
    generateSearchKey(query: string, page: number, size: number, topK: number): string {
        return `search:${query}:${page}:${size}:${topK}`;
    }

    // Generate cache key for suggestions
    generateSuggestionsKey(query: string, userId: string): string {
        return `suggestions:${query}:${userId}`;
    }

    // Clean up expired items
    cleanup(): void {
        const now = Date.now();
        const keysToDelete: string[] = [];

        this.cache.forEach((item, key) => {
            if (now - item.timestamp > item.ttl) {
                keysToDelete.push(key);
            }
        });

        keysToDelete.forEach(key => this.delete(key));
    }
}