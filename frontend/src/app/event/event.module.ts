import {NgModule} from '@angular/core';
import { MatFormFieldModule, MatIconModule, MatSelectModule, MatDatepickerModule,
  MatCardModule, MatDividerModule, MatPaginatorModule, MatProgressSpinnerModule,
  MatInputModule, MatButtonModule, MatNativeDateModule, MatCheckboxModule } from '@angular/material';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { EventDetailsComponent } from './event-details/event-details.component';
import { SeatsDisplayComponent } from './event-details/seats-display/seats-display.component';
import { EventFormComponent } from './event-form/event-form.component';
import { EventItemComponent } from './event-list/event-item/event-item.component';
import { EventListComponent } from './event-list/event-list.component';
import { EventSearchComponent } from './event-search/event-search.component';
import { EventComponent } from './event.component';
import { RouterModule } from '@angular/router';
import { AgmMap, AgmMarker, AgmCoreModule } from '@agm/core';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatIconModule,
    MatSelectModule,
    MatDatepickerModule,
    MatCardModule,
    MatDividerModule,
    RouterModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatInputModule,
    MatButtonModule,
    MatNativeDateModule,
    MatCheckboxModule,
    AgmCoreModule
  ],
  declarations: [
    EventDetailsComponent,
    SeatsDisplayComponent,
    EventFormComponent,
    EventListComponent,
    EventItemComponent,
    EventSearchComponent,
    EventComponent,
  ],
  exports: [
    EventDetailsComponent,
    SeatsDisplayComponent,
    EventFormComponent,
    EventListComponent,
    EventItemComponent,
    EventSearchComponent,
    EventComponent,
  ],
})
export class EventModule {}
