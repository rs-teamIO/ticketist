import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import { PORT } from '../shared/constants';

export interface ITicket {
  id: number;
  numberRow: number;
  numberColumn: number;
  price: number;
  status: string;
  eventSectorId: number;
  eventId: number;
  userId: number;
  eventName: string;
  venueName: string;
  sectorName: string;
  date: Date;
}

@Injectable({providedIn: 'root'})
export class TicketService {
  ticketsSelected = new Subject<ITicket[]>();

  private readonly getEventSectorTicketsPath = `http://localhost:${PORT}/api/tickets/event/sector/`;
  private readonly getReservationTicketsPath = `http://localhost:${PORT}/api/tickets/reservations/`;

  constructor(private http: HttpClient) {
  }

  getEventSectorTickets(eventSectorId: number): Observable<ITicket[]> {
    return this.http.get<ITicket[]>(this.getEventSectorTicketsPath + eventSectorId);
  }

  getReservationTickets(resId: number): Observable<ITicket[]> {
    return this.http.get<ITicket[]>(this.getReservationTicketsPath + resId);
  }
}
