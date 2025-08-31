export const environment = {
    production: false,
    apiUrl: '/api', // Use relative URL to leverage proxy
    retryAttempts: 3,
    retryDelay: 1000,
    requestTimeout: 30000,
    cacheTimeout: 300000, // 5 minutes
    debounceTime: 300
};