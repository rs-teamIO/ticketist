import {EventService, IEvent} from './event.service';
import {fakeAsync, inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {PORT} from '../shared/constants';
import {IEventModel} from "../model/event.model";
import {HttpRequest} from "@angular/common/http";


describe('EventService', () => {
  let service: EventService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EventService]
    });

    service = TestBed.get(EventService);
    httpMock = TestBed.get(HttpTestingController);

  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be crated', inject([EventService], (ser: EventService) => {
    expect(ser).toBeTruthy();
  }));

  it('should create event when body is valid', () => {
    const eventToBeCreated: IEvent = {
      basicInfo: {
        venueId: 1,
        name: 'Event name',
        category: 'CULTURAL',
        startDate: new Date().getTime(),
        endDate: new Date().getTime() + 3000,
        reservationDeadline: new Date().getTime() - 3000,
        description: 'Desc',
        reservationLimit: 3
      },
      eventSectors: [],
      mediaFiles: []
    };

    service.createEvent(eventToBeCreated).subscribe((createdEvent: IEventModel) => {
      expect(createdEvent.id).toBe(1);
    });

    const postEventPath = `http://localhost:${PORT}/api/events`;
    const request = httpMock.expectOne(postEventPath);
    expect(request.request.method).toBe('POST');
    request.flush({id: 1, ...eventToBeCreated.basicInfo, eventSectors: [], mediaFiles: []});
  });

  it('should return all events when searchParams are empty', () => {
    const events: IEventModel[] = [
      {
        id: 1,
        name: 'Event1 name',
        venueName: 'Spens',
        venueId: 1,
        startDate: new Date(),
        endDate: new Date(),
        description: 'Desc',
        reservationLimit: 3,
        eventSectors: [],
        mediaFiles: []
      },
      {
        id: 2,
        name: 'Event2 name',
        venueName: 'Spens',
        venueId: 1,
        startDate: new Date(),
        endDate: new Date(),
        description: 'Desc',
        reservationLimit: 3,
        eventSectors: [],
        mediaFiles: []
      }
    ];
    const searchParams = {
      eventName: '',
      category: '',
      venueName: '',
      startDate: null,
      endDate: null
    };

    service.getEvents();

    const getEventsPath = `http://localhost:${PORT}/api/events/paged`;
    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/paged?page=0&size=8`);
    expect(request.request.method).toBe('GET');
    request.flush(events);
  });


});
