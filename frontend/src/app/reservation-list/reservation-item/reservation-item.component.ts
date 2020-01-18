import { Component, OnInit, Input } from '@angular/core';
import { IReservation } from 'src/app/services/reservation.service';

@Component({
  selector: 'app-reservation-item',
  templateUrl: './reservation-item.component.html',
  styleUrls: ['./reservation-item.component.scss']
})
export class ReservationItemComponent implements OnInit {
  @Input() reservation: IReservation;

  constructor() { }

  ngOnInit() {
  }

}
