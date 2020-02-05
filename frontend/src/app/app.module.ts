import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HeaderComponent} from './header/header.component';
import {LoginComponent} from './auth/login/login.component';
import {SignUpComponent} from './auth/sign-up/sign-up.component';
import {EventComponent} from './event/event.component';
import {EventListComponent} from './event/event-list/event-list.component';
import {EventSearchComponent} from './event/event-search/event-search.component';
import {EventFormComponent} from './event/event-form/event-form.component';
import {EventFormBasicComponent} from './event/event-form/event-form-basic/event-form-basic.component';
import {EventFormMediaComponent} from './event/event-form/event-form-media/event-form-media.component';
import {EventFormSectorsComponent} from './event/event-form/event-form-sectors/event-form-sectors.component';
import {VenueComponent} from './venue/venue.component';
import {VenueListComponent} from './venue/venue-list/venue-list.component';
import {VenueFormComponent} from './venue/venue-form/venue-form.component';
import {ReportComponent} from './report/report.component';
import {ReportChartComponent} from './report/report-chart/report-chart.component';
import {ReportTableComponent} from './report/report-table/report-table.component';
import {EventDetailsComponent} from './event/event-details/event-details.component';
import {SeatsDisplayComponent} from './event/event-details/seats-display/seats-display.component';
import {ReservationListComponent} from './reservation-list/reservation-list.component';
import {ReservationItemComponent} from './reservation-list/reservation-item/reservation-item.component';
import {EventItemComponent} from './event/event-list/event-item/event-item.component';
import {VenueItemComponent} from './venue/venue-list/venue-item/venue-item.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {UserProfileComponent} from './user-profile/user-profile.component';
import {ReactiveFormsModule} from '@angular/forms';
import {CustomMaterialModule} from './shared/material.module';
import {AuthInterceptorService} from './services/auth-interceptor.service';
import {NgxChartsModule} from '@swimlane/ngx-charts';
import {ToMonthChartPipe} from './pipes/to-month-chart.pipe';
import {ToInitialChartPipe} from './pipes/to-initial-chart.pipe';
import {ErrorPageComponent} from './error-page/error-page.component';
import {TicketItemComponent} from './checkout/ticket-item/ticket-item.component';
import {AgmCoreModule} from '@agm/core';
import {BrowserModule} from '@angular/platform-browser';
import {MapViewComponent} from './venue/venue-form/map-view/map-view.component';
import {GridsterModule} from 'angular-gridster2';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    LoginComponent,
    SignUpComponent,
    EventComponent,
    EventListComponent,
    EventSearchComponent,
    EventFormComponent,
    EventFormBasicComponent,
    EventFormMediaComponent,
    EventFormSectorsComponent,
    VenueComponent,
    VenueListComponent,
    VenueFormComponent,
    ReportComponent,
    ReportChartComponent,
    ReportTableComponent,
    EventDetailsComponent,
    SeatsDisplayComponent,
    ReservationListComponent,
    ReservationItemComponent,
    EventItemComponent,
    VenueItemComponent,
    CheckoutComponent,
    TicketItemComponent,
    UserProfileComponent,
    ToMonthChartPipe,
    ToInitialChartPipe,
    ErrorPageComponent,
    MapViewComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        CustomMaterialModule,
        HttpClientModule,
        NgxChartsModule,
        AgmCoreModule.forRoot({
            apiKey: 'AIzaSyCn40xrisQWoIytZzAEohvAWPQfTIk1SR4',
            libraries: ['places', 'geometry']
            /* apiKey is required, unless you are a premium customer, in which case you can use clientId */
        }),
        GridsterModule,
    ],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule {
}
