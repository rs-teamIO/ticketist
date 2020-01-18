import { Component, OnInit, Input } from '@angular/core';
import { IReservation, ReservationService } from 'src/app/services/reservation.service';

@Component({
  selector: 'app-reservation-item',
  templateUrl: './reservation-item.component.html',
  styleUrls: ['./reservation-item.component.scss']
})
export class ReservationItemComponent implements OnInit {
  @Input() reservation: IReservation;
  errorMessage = '';

  constructor(private reservationService: ReservationService) { }

  ngOnInit() {
  }

  cancelReservation() {
    this.reservationService.cancelReservation(this.reservation.id)
    .subscribe(resData => {
      console.log(resData);
      this.reservationService.getReservations();
    },
    error => {
      console.log('Error: ', error);
      if (error.status === 400) {
        this.errorMessage = error.error.message;
      }
    }
    );
  }

}
