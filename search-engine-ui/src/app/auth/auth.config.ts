import { AuthConfig } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';

export const authConfig: AuthConfig = {
    issuer: `${environment.apiUrl}/auth-server`,
    clientId: 'search-engine-ui',
    responseType: 'code',
    redirectUri: window.location.origin + '/auth/callback',
    postLogoutRedirectUri: window.location.origin,
    scope: 'openid profile read write',
    showDebugInformation: !environment.production,
    requireHttps: false,
    strictDiscoveryDocumentValidation: false,
    skipIssuerCheck: true
};