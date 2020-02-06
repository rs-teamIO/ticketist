import { Injectable } from '@angular/core';
import { Page } from '../model/page.model';
import { Subject } from 'rxjs';
import { PageEvent } from '@angular/material';
import { PORT } from '../shared/constants';
import { HttpClient, HttpParams } from '@angular/common/http';

export interface IReservation {
  id: number;
  eventName: string;
  venueName: string;
  price: number;
  ticketCount: number;
  eventId?: number;
}

export interface IReservationPage {
  reservations: IReservation[];
  totalSize: number;
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  reservationsChanged = new Subject<IReservationPage>();
  private page: Page = new Page(0, 3);
  pageChanged = new Subject<PageEvent>();

  private readonly getReservationsPath = `http://localhost:${PORT}/api/tickets/reservations`;
  private readonly cancelReservationPath = `http://localhost:${PORT}/api/tickets/reservations/cancel/`;

  constructor(private http: HttpClient) {
    this.pageChanged.subscribe((value: any) => {
      this.page = { page: value.pageIndex, size: value.pageSize };
    });
  }

  getReservations(): void {
    const params: HttpParams = new HttpParams().set('page', this.page.page + '').set('size', this.page.size + '');
    this.http.get<IReservationPage>(this.getReservationsPath, { params })
    .subscribe((responseData: IReservationPage) => {
      this.reservationsChanged.next(responseData);
    });
  }

  cancelReservation(reservationID: number) {
    return this.http.post<boolean>(
      this.cancelReservationPath + (reservationID + ''), {}
    );
  }


}
