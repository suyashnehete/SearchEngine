import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';

@Component({
    selector: 'app-auth-callback',
    template: `
    <div class="d-flex justify-content-center align-items-center" style="height: 100vh;">
      <div class="text-center">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3">Processing authentication...</p>
      </div>
    </div>
  `
})
export class AuthCallbackComponent implements OnInit {

    constructor(
        private oauthService: OAuthService,
        private router: Router
    ) { }

    ngOnInit() {
        this.oauthService.loadDiscoveryDocumentAndTryLogin().then(() => {
            if (this.oauthService.hasValidAccessToken()) {
                this.router.navigate(['/']);
            } else {
                this.router.navigate(['/login']);
            }
        });
    }
}