import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/search/search.component';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { CrawlerComponent } from './components/crawler/crawler.component';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { LoginComponent } from './auth/login.component';
import { AuthCallbackComponent } from './auth/auth-callback.component';
import { UnauthorizedComponent } from './auth/unauthorized.component';
import { NavbarComponent } from './components/navbar.component';
import { AdminPanelComponent } from './components/admin-panel.component';

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    CrawlerComponent,
    LoginComponent,
    AuthCallbackComponent,
    UnauthorizedComponent,
    NavbarComponent,
    AdminPanelComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    OAuthModule.forRoot()
  ],
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    AuthService,
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
