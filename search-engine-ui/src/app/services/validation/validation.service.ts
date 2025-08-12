import { Injectable } from '@angular/core';

export interface ValidationResult {
    isValid: boolean;
    errors: string[];
}

@Injectable({
    providedIn: 'root'
})
export class ValidationService {

    validateUrl(url: string): ValidationResult {
        const errors: string[] = [];

        if (!url || !url.trim()) {
            errors.push('URL is required');
            return { isValid: false, errors };
        }

        const trimmedUrl = url.trim();

        // Check if URL starts with http:// or https://
        if (!trimmedUrl.match(/^https?:\/\//)) {
            errors.push('URL must start with http:// or https://');
        }

        // Validate URL format
        try {
            const urlObj = new URL(trimmedUrl);

            // Check for valid hostname
            if (!urlObj.hostname || urlObj.hostname.length < 3) {
                errors.push('Invalid hostname');
            }

            // Check for suspicious patterns
            if (this.containsSuspiciousPatterns(trimmedUrl)) {
                errors.push('URL contains suspicious patterns');
            }

            // Check URL length
            if (trimmedUrl.length > 2048) {
                errors.push('URL is too long (maximum 2048 characters)');
            }

        } catch (e) {
            errors.push('Invalid URL format');
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    validateSearchQuery(query: string): ValidationResult {
        const errors: string[] = [];

        if (!query || !query.trim()) {
            errors.push('Search query is required');
            return { isValid: false, errors };
        }

        const trimmedQuery = query.trim();

        // Check query length
        if (trimmedQuery.length < 1) {
            errors.push('Search query must be at least 1 character');
        }

        if (trimmedQuery.length > 500) {
            errors.push('Search query is too long (maximum 500 characters)');
        }

        // Check for suspicious patterns
        if (this.containsSuspiciousPatterns(trimmedQuery)) {
            errors.push('Search query contains invalid characters');
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    validateUserId(userId: string): ValidationResult {
        const errors: string[] = [];

        if (!userId || !userId.trim()) {
            errors.push('User ID is required');
            return { isValid: false, errors };
        }

        const trimmedUserId = userId.trim();

        // Check length
        if (trimmedUserId.length < 1 || trimmedUserId.length > 100) {
            errors.push('User ID must be between 1 and 100 characters');
        }

        // Check for valid characters (alphanumeric, underscore, hyphen)
        if (!trimmedUserId.match(/^[a-zA-Z0-9_-]+$/)) {
            errors.push('User ID can only contain letters, numbers, underscores, and hyphens');
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    validatePagination(page: number, size: number): ValidationResult {
        const errors: string[] = [];

        if (!Number.isInteger(page) || page < 1) {
            errors.push('Page must be a positive integer');
        }

        if (!Number.isInteger(size) || size < 1 || size > 100) {
            errors.push('Page size must be between 1 and 100');
        }

        return {
            isValid: errors.length === 0,
            errors
        };
    }

    sanitizeHtml(input: string): string {
        const div = document.createElement('div');
        div.textContent = input;
        return div.innerHTML;
    }

    private containsSuspiciousPatterns(input: string): boolean {
        const suspiciousPatterns = [
            /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,
            /javascript:/gi,
            /vbscript:/gi,
            /onload=/gi,
            /onerror=/gi,
            /onclick=/gi,
            /<iframe/gi,
            /<object/gi,
            /<embed/gi
        ];

        return suspiciousPatterns.some(pattern => pattern.test(input));
    }
}