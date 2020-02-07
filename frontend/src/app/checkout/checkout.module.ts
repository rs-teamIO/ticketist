import {NgModule} from '@angular/core';
import { MatFormFieldModule, MatCardModule, MatButtonModule } from '@angular/material';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { TicketItemComponent } from './ticket-item/ticket-item.component';
import { CheckoutComponent } from './checkout.component';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatCardModule,
    MatButtonModule
  ],
  declarations: [
    TicketItemComponent,
    CheckoutComponent
  ],
  exports: [
    TicketItemComponent,
    CheckoutComponent
  ],
})
export class CheckoutModule {}
