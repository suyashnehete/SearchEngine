import { authConfig } from './auth.config';
import { environment } from '../../environments/environment';

describe('AuthConfig', () => {
    it('should use gateway-based auth server URL', () => {
        expect(authConfig.issuer).toBe(`${environment.apiUrl}/auth-server`);
    });

    it('should construct issuer URL correctly', () => {
        const expectedUrl = 'http://localhost:8081/api/auth-server';
        expect(authConfig.issuer).toBe(expectedUrl);
    });

    it('should maintain other OAuth2 configuration properties', () => {
        expect(authConfig.clientId).toBe('search-engine-ui');
        expect(authConfig.responseType).toBe('code');
        expect(authConfig.scope).toBe('openid profile read write');
    });

    it('should have correct redirect URIs', () => {
        expect(authConfig.redirectUri).toBe(window.location.origin + '/auth/callback');
        expect(authConfig.postLogoutRedirectUri).toBe(window.location.origin);
    });
});