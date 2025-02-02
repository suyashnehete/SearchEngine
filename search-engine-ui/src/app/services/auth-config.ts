import { AuthConfig } from "angular-oauth2-oidc";

export const authConfig: AuthConfig = {
    issuer: 'https://accounts.google.com',
    clientId: 'YOUR_CLIENT_ID',
    redirectUri: window.location.origin,
    scope: 'openid email profile',
  };