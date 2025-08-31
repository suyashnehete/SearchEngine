import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
    isAuthenticated$: Observable<boolean>;
    userRoles$: Observable<string[]>;
    userInfo$: Observable<any>;
    isAdmin$: Observable<boolean>;
    showModal = false;
    private pendingAdminNavigation = false;

    constructor(
        public authService: AuthService,
        private router: Router
    ) {
        this.isAuthenticated$ = this.authService.isAuthenticated$;
        this.userRoles$ = this.authService.userRoles$;
        this.userInfo$ = this.authService.userInfo$;
        this.isAdmin$ = this.userRoles$.pipe(
            map(roles => roles.includes('ADMIN'))
        );
    }

    ngOnInit() { }

    showLoginModal() {
        this.showModal = true;
    }

    showAdminLogin(event: Event) {
        event.preventDefault();
        this.pendingAdminNavigation = true;
        this.showModal = true;
    }

    onLoginSuccess() {
        if (this.pendingAdminNavigation && this.authService.isAdmin()) {
            this.router.navigate(['/admin']);
            this.pendingAdminNavigation = false;
        }
    }

    onModalClosed() {
        this.showModal = false;
        this.pendingAdminNavigation = false;
    }

    logout() {
        this.authService.logout().subscribe();
    }
}