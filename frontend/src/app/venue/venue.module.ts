import {NgModule} from '@angular/core';
import { MatFormFieldModule, MatCardModule, MatPaginatorModule, MatIconModule, MatButtonModule, MatInputModule } from '@angular/material';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { VenueFormComponent } from './venue-form/venue-form.component';
import { VenueItemComponent } from './venue-list/venue-item/venue-item.component';
import { VenueListComponent } from './venue-list/venue-list.component';
import { RouterModule } from '@angular/router';
import { GridsterModule } from 'angular-gridster2';
import { AgmCoreModule } from '@agm/core';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatCardModule,
    MatPaginatorModule,
    RouterModule,
    GridsterModule,
    MatIconModule,
    MatButtonModule,
    MatInputModule,
    AgmCoreModule
  ],
  declarations: [
    VenueFormComponent,
    VenueListComponent,
    VenueItemComponent,
  ],
  exports: [
    VenueFormComponent,
    VenueListComponent,
    VenueItemComponent
  ],
})
export class VenueModule {}
