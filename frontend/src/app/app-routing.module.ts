import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EventComponent } from './event/event.component';
import { LoginComponent } from './auth/login/login.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { RegisteredUserAuthGuard, AdminAuthGuard } from './auth/auth.guard';
import { EventFormComponent } from './event/event-form/event-form.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { VenueListComponent } from './venue/venue-list/venue-list.component';
import { VenueFormComponent } from './venue/venue-form/venue-form.component';
import { ReservationListComponent } from './reservation-list/reservation-list.component';
import { EventDetailsComponent } from './event/event-details/event-details.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {ReportComponent} from './report/report.component';

const routes: Routes = [
  { path: '', redirectTo: '/events', pathMatch: 'full' },
  { path: 'events', component: EventComponent },
  { path: 'events/new', component: EventFormComponent, canActivate: [AdminAuthGuard]},
  { path: 'venues/list', component: VenueListComponent, canActivate: [AdminAuthGuard] },
  { path: 'venues/new', component: VenueFormComponent, canActivate: [AdminAuthGuard] },
  { path: 'venues/:id', component: VenueFormComponent, canActivate: [AdminAuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignUpComponent },
  { path: 'profile', component: UserProfileComponent, canActivate: [RegisteredUserAuthGuard] },
  { path: 'my-reservations', component: ReservationListComponent, canActivate: [RegisteredUserAuthGuard] },
  { path: 'not-found', component: ErrorPageComponent },
  { path: 'event/:id', component: EventDetailsComponent },
  { path: 'checkout', component: CheckoutComponent, canActivate: [RegisteredUserAuthGuard] },
  { path: 'checkout/:resId', component: CheckoutComponent, canActivate: [RegisteredUserAuthGuard] },
  { path: 'reports', component: ReportComponent, canActivate: [AdminAuthGuard] },
  { path: '**', redirectTo: '/not-found'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
