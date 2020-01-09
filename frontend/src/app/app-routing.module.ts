import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {EventComponent} from './event/event.component';
import {VenueComponent} from './venue/venue.component';
import {LoginComponent} from './auth/login/login.component';
import {SignUpComponent} from './auth/sign-up/sign-up.component';
import {UserProfileComponent} from './user-profile/user-profile.component';
import {AuthGuard} from './auth/auth.guard';
import {ReportComponent} from './report/report.component';


const routes: Routes = [
  {path: '', redirectTo: '/events', pathMatch: 'full'},
  {path: 'events', component: EventComponent},
  {path: 'venues', component: VenueComponent},
  {path: 'login', component: LoginComponent},
  {path: 'signup', component: SignUpComponent},
  {path: 'reports', component: ReportComponent},
  {path: 'profile', component: UserProfileComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
