import { Component, OnInit, OnDestroy } from '@angular/core';
import { IReservation, ReservationService, IReservationPage } from '../services/reservation.service';
import { Subscription } from 'rxjs';
import { PageEvent } from '@angular/material';

@Component({
  selector: 'app-reservation-list',
  templateUrl: './reservation-list.component.html',
  styleUrls: ['./reservation-list.component.scss']
})
export class ReservationListComponent implements OnInit, OnDestroy {

  reservations: IReservation[] = [];
  totalSize: number;
  subscription: Subscription;

  constructor(private reservationService: ReservationService) { }

  ngOnInit() {
    this.subscription = this.reservationService.reservationsChanged
    .subscribe((reservationPage: IReservationPage) => {
      this.reservations = reservationPage.reservations;
      this.totalSize = reservationPage.totalSize;
    });
    this.reservationService.getReservations();
  }

  onPageChanged(pageEvent: PageEvent) {
    this.reservationService.pageChanged.next(pageEvent);
    this.reservationService.getReservations();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
