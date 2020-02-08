import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AuthInterceptorService} from './services/auth-interceptor.service';
import {ToMonthChartPipe} from './pipes/to-month-chart.pipe';
import {ToInitialChartPipe} from './pipes/to-initial-chart.pipe';
import {AgmCoreModule, AgmMarker, AgmMap} from '@agm/core';
import {BrowserModule} from '@angular/platform-browser';
import { AuthModule } from './auth/auth.module';
import { CheckoutModule } from './checkout/checkout.module';
import { ErrorPageModule } from './error-page/error-page.module';
import { EventModule } from './event/event.module';
import { HeaderModule } from './header/header.module';
import { ReportModule } from './report/report.module';
import { ReservationModule } from './reservation-list/reservation.module';
import { UserProfileModule } from './user-profile/user-profile.module';
import { VenueModule } from './venue/venue.module';
import { CustomMaterialModule } from './shared/material.module';

@NgModule({
  declarations: [
    AppComponent,
    ToMonthChartPipe,
    ToInitialChartPipe,
  ],
  imports: [
      BrowserModule,
      AppRoutingModule,
      BrowserAnimationsModule,
      // CustomMaterialModule,
      HttpClientModule,
      AgmCoreModule.forRoot({
          apiKey: 'AIzaSyCn40xrisQWoIytZzAEohvAWPQfTIk1SR4',
          libraries: ['places', 'geometry']
          /* apiKey is required, unless you are a premium customer, in which case you can use clientId */
      }),
      AuthModule,
      CheckoutModule,
      ErrorPageModule,
      EventModule,
      HeaderModule,
      ReportModule,
      ReservationModule,
      UserProfileModule,
      VenueModule
  ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
