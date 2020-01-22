import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { EventComponent } from './event/event.component';
import { VenueComponent } from './venue/venue.component';
import { LoginComponent } from './auth/login/login.component';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { AuthGuard } from './auth/auth.guard';
import { EventFormComponent } from './event/event-form/event-form.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import {VenueListComponent} from './venue/venue-list/venue-list.component';
import {VenueFormComponent} from './venue/venue-form/venue-form.component';
import { ReservationListComponent } from './reservation-list/reservation-list.component';
import { EventDetailsComponent } from './event/event-details/event-details.component';
import {CheckoutComponent} from './checkout/checkout.component';

const routes: Routes = [
  { path: '', redirectTo: '/events', pathMatch: 'full' },
  { path: 'events', component: EventComponent },
  { path: 'events/new', component: EventFormComponent, canActivate: [AuthGuard]},
  { path: 'venues', component: VenueComponent },
  { path: 'venues/list', component: VenueListComponent },
  { path: 'venues/new', component: VenueFormComponent },
  { path: 'venues/:id', component: VenueFormComponent},
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignUpComponent },
  { path: 'profile', component: UserProfileComponent, canActivate: [AuthGuard] },
  { path: 'my-reservations', component: ReservationListComponent, canActivate: [AuthGuard] },
  { path: 'not-found', component: ErrorPageComponent },
  { path: 'event-details', component: EventDetailsComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'checkout/:resId', component: CheckoutComponent },
  { path: '**', redirectTo: '/not-found'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
