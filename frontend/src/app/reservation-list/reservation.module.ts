import {NgModule} from '@angular/core';
import { MatFormFieldModule, MatCardModule, MatPaginatorModule, MatButtonModule } from '@angular/material';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { ReservationItemComponent } from './reservation-item/reservation-item.component';
import { ReservationListComponent } from './reservation-list.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatCardModule,
    MatPaginatorModule,
    RouterModule,
    MatButtonModule
  ],
  declarations: [
    ReservationItemComponent,
    ReservationListComponent
  ],
  exports: [
    ReservationItemComponent,
    ReservationListComponent
  ],
})
export class ReservationModule {}
