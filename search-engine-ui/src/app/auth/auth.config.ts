import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
    issuer: 'http://localhost:8080',
    clientId: 'search-engine-ui',
    responseType: 'code',
    redirectUri: window.location.origin + '/auth/callback',
    postLogoutRedirectUri: window.location.origin,
    scope: 'openid profile read write',
    showDebugInformation: true,
    requireHttps: false,
    strictDiscoveryDocumentValidation: false,
    skipIssuerCheck: true
};