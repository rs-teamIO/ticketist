import {ITicket, TicketService} from './ticket.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {inject, TestBed, tick, fakeAsync} from '@angular/core/testing';
import {PORT} from '../shared/constants';
import {HttpResponse} from '@angular/common/http';

describe('TicketService', () => {
  let ticketService: TicketService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TicketService]
    });

    ticketService = TestBed.get(TicketService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', inject([TicketService], (service: TicketService) => {
    expect(service).toBeTruthy();
  }));

  it('should get tickets that belong to event sector with given id', fakeAsync(() => {
    const eventSectorId = 1;

    let ticketList: ITicket[];
    ticketService.getEventSectorTickets(eventSectorId).subscribe((responseData: ITicket[]) => {
      ticketList = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/tickets/event/sector/` + eventSectorId);
    expect(request.request.method).toBe('GET');
    request.flush([
      {
        id: 1,
        numberRow: 1,
        numberColumn: 1,
        price: 35,
        status: 'PAID',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      },
      {
        id: 2,
        numberRow: 2,
        numberColumn: 2,
        price: 35,
        status: 'RESERVED',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      }]);

    tick();
    expect(ticketList).toBeDefined();
    expect(ticketList.length).toEqual(2);
    expect(ticketList[0].id).toEqual(1);
    expect(ticketList[0].eventSectorId).toEqual(eventSectorId);
    expect(ticketList[1].id).toEqual(2);
    expect(ticketList[1].eventSectorId).toEqual(eventSectorId);
  }));

  it('should get tickets that belong to reservation with given id', fakeAsync(() => {
    const reservationId = 1;

    let reservationTickets: ITicket[];
    ticketService.getReservationTickets(reservationId).subscribe((responseData: ITicket[]) => {
      reservationTickets = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/tickets/reservations/` + reservationId);
    expect(request.request.method).toBe('GET');
    request.flush([
      {
        id: 1,
        numberRow: 1,
        numberColumn: 1,
        price: 35,
        status: 'RESERVED',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      },
      {
        id: 2,
        numberRow: 2,
        numberColumn: 2,
        price: 35,
        status: 'RESERVED',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      }]);

    tick();
    expect(reservationTickets).toBeDefined();
    expect(reservationTickets.length).toEqual(2);
    expect(reservationTickets[0].id).toEqual(1);
    expect(reservationTickets[0].eventName).toEqual('Event name');
    expect(reservationTickets[1].id).toEqual(2);
    expect(reservationTickets[1].eventName).toEqual('Event name');
  }));

  it('should buy tickets', fakeAsync(() => {
    const reservation = false;
    const ticketList: number[] = [1, 2];

    let boughtTickets: ITicket[];
    ticketService.buyTickets(ticketList, reservation).subscribe((responseData: ITicket[]) => {
      boughtTickets = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/tickets/`);
    httpMock.expectNone(`http://localhost:${PORT}/api/tickets/reservations/accept/`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(ticketList);
    request.flush([
      {
        id: 1,
        numberRow: 1,
        numberColumn: 1,
        price: 35,
        status: 'PAID',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      },
      {
        id: 2,
        numberRow: 2,
        numberColumn: 2,
        price: 35,
        status: 'PAID',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      }]);

    tick();
    expect(boughtTickets).toBeDefined();
    expect(boughtTickets.length).toEqual(2);
    expect(boughtTickets[0].id).toEqual(1);
    expect(boughtTickets[1].id).toEqual(2);
  }));

  it('should buy tickets that were previously reserved', fakeAsync(() => {
    const reservation = true;
    const reservationId = 1;
    const ticketList: number[] = [];

    let ticketsBought;
    ticketService.buyTickets(ticketList, reservation, reservationId).subscribe(responseData => {
      ticketsBought = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/tickets/reservations/accept/` + reservationId);
    httpMock.expectNone(`http://localhost:${PORT}/api/tickets/`) ;
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.event(new HttpResponse<boolean>({body: true}));

    tick();
    expect(ticketsBought).toBeDefined();
    expect(ticketsBought).toBe(true);
  }));

  it('should make reservations', fakeAsync(() => {
    const ticketsToBeReserved = [1, 2];

    let reservedTickets: ITicket[];
    ticketService.reserveTickets(ticketsToBeReserved).subscribe((responseData: ITicket[]) => {
      reservedTickets = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/tickets/reservations`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(ticketsToBeReserved);
    request.flush([
      {
        id: 1,
        numberRow: 1,
        numberColumn: 1,
        price: 35,
        status: 'RESERVED',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      },
      {
        id: 2,
        numberRow: 2,
        numberColumn: 2,
        price: 35,
        status: 'RESERVED',
        eventSectorId: 1,
        eventId: 1,
        userId: 1,
        eventName: 'Event name',
        venueName: 'Venue name',
        sectorName: 'Sector name',
        date: new Date()
      }]);

    tick();
    expect(reservedTickets).toBeDefined();
    expect(reservedTickets.length).toEqual(2);
    expect(reservedTickets[0].id).toEqual(1);
    expect(reservedTickets[0].status).toEqual('RESERVED');
    expect(reservedTickets[1].id).toEqual(2);
    expect(reservedTickets[1].status).toEqual('RESERVED');
  }));
});
