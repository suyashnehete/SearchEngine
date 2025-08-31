import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { OAuthModule } from 'angular-oauth2-oidc';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './components/search/search.component';
import { FormsModule } from '@angular/forms';
import { CrawlerComponent } from './components/crawler/crawler.component';
import { AuthService } from './auth/auth.service';
import { AuthGuard } from './auth/auth.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { LoginComponent } from './auth/login.component';
import { AuthCallbackComponent } from './auth/auth-callback.component';
import { UnauthorizedComponent } from './auth/unauthorized.component';
import { NavbarComponent } from './components/navbar.component';
import { AdminPanelComponent } from './components/admin-panel.component';
import { LoginModalComponent } from './components/login-modal.component';
import { StartupGuideComponent } from './components/startup-guide.component';

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    CrawlerComponent,
    LoginComponent,
    AuthCallbackComponent,
    UnauthorizedComponent,
    NavbarComponent,
    AdminPanelComponent,
    LoginModalComponent,
    StartupGuideComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    OAuthModule.forRoot()
  ],
  providers: [
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
