import { Injectable } from '@angular/core';
import { ApiError } from '../base-service';

export interface UserFriendlyError {
    title: string;
    message: string;
    actionable: boolean;
    retryable: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class ErrorHandlerService {

    handleApiError(error: ApiError): UserFriendlyError {
        switch (error.status) {
            case 0:
                return {
                    title: 'Connection Error',
                    message: 'Unable to connect to the server. Please check your internet connection and try again.',
                    actionable: true,
                    retryable: true
                };

            case 400:
                return {
                    title: 'Invalid Request',
                    message: error.message || 'The request contains invalid data. Please check your input and try again.',
                    actionable: true,
                    retryable: false
                };

            case 401:
                return {
                    title: 'Authentication Required',
                    message: 'You need to log in to access this feature.',
                    actionable: true,
                    retryable: false
                };

            case 403:
                return {
                    title: 'Access Denied',
                    message: 'You don\'t have permission to perform this action.',
                    actionable: false,
                    retryable: false
                };

            case 404:
                return {
                    title: 'Not Found',
                    message: 'The requested resource could not be found.',
                    actionable: false,
                    retryable: false
                };

            case 429:
                return {
                    title: 'Too Many Requests',
                    message: 'You\'re making requests too quickly. Please wait a moment and try again.',
                    actionable: true,
                    retryable: true
                };

            case 500:
                return {
                    title: 'Server Error',
                    message: 'An internal server error occurred. Our team has been notified.',
                    actionable: false,
                    retryable: true
                };

            case 502:
            case 503:
            case 504:
                return {
                    title: 'Service Unavailable',
                    message: 'The service is temporarily unavailable. Please try again in a few moments.',
                    actionable: true,
                    retryable: true
                };

            default:
                return {
                    title: 'Unexpected Error',
                    message: error.message || 'An unexpected error occurred. Please try again.',
                    actionable: true,
                    retryable: true
                };
        }
    }

    logError(error: any, context?: string): void {
        const errorInfo = {
            error: error,
            context: context,
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            url: window.location.href
        };

        console.error('Application Error:', errorInfo);

    }

}