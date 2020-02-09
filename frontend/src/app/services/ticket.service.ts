import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {PORT} from '../shared/constants';

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
  private readonly buyTicketsPath = `http://localhost:${PORT}/api/tickets/`;
  private readonly acceptReservationPath = `http://localhost:${PORT}/api/tickets/reservations/accept/`;
  private readonly reserveTicketsPath = `http://localhost:${PORT}/api/tickets/reservations`;

  constructor(private http: HttpClient) {
  }

  getEventSectorTickets(eventSectorId: number): Observable<ITicket[]> {
    return this.http.get<ITicket[]>(this.getEventSectorTicketsPath + eventSectorId);
  }

  getReservationTickets(resId: number): Observable<ITicket[]> {
    return this.http.get<ITicket[]>(this.getReservationTicketsPath + resId);
  }

  buyTickets(ticketList: number[], reservation: boolean, resId?: number): Observable<ITicket[]> {
    if (reservation) {
      return this.http.post<ITicket[]>(this.acceptReservationPath + resId, {});
    } else {
      return this.http.post<ITicket[]>(this.buyTicketsPath, ticketList);
    }
  }

  reserveTickets(ticketList: number[]): Observable<ITicket[]> {
    return this.http.post<ITicket[]>(this.reserveTicketsPath, ticketList);
  }
}
