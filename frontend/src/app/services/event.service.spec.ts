import {EventService, IEvent, IEventPage, IMediaFile, ISearchParams} from './event.service';
import {fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {PORT} from '../shared/constants';
import {IEventModel} from '../model/event.model';
import {HttpHeaders} from '@angular/common/http';

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

  it('should be created', inject([EventService], (ser: EventService) => {
    expect(ser).toBeTruthy();
  }));

  it('should create event when body is valid', fakeAsync(() => {
    const event: IEvent = {
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

    let createdEvent: IEventModel;
    service.createEvent(event).subscribe((responseData: IEventModel) => {
      createdEvent = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events`);
    expect(request.request.method).toBe('POST');
    request.flush({id: 1, ...event.basicInfo, eventSectors: [], mediaFiles: []});
    tick();
    expect(createdEvent).toBeDefined();
    expect(createdEvent.id).toEqual(1);
    expect(createdEvent.name).toEqual('Event name');
  }));

  it('should return all events when searchParams are empty', fakeAsync(() => {
    let eventPage;
    service.eventsChanged.subscribe((responseData: IEventPage) => {
      eventPage = responseData;
    });

    service.getEvents();

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/paged?page=0&size=8`);
    expect(request.request.method).toBe('GET');
    request.flush({
      events: [
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
      ],
      totalSize: 2
    });

    tick();
    expect(eventPage).toBeDefined();
    expect(eventPage.totalSize).toEqual(2);
    expect(eventPage.events.length).toEqual(2);
    expect(eventPage.events[0].id).toEqual(1);
    expect(eventPage.events[0].name).toEqual('Event1 name');

    expect(eventPage.events[1].id).toEqual(2);
    expect(eventPage.events[1].name).toEqual('Event2 name');
  }));

  it('should return search results when searchParams are not empty', fakeAsync(() => {
    let searchParams;
    let eventPage;

    service.searchParamsChanged.subscribe((responseData: ISearchParams) => {
      searchParams = responseData;
    });
    service.searchParamsChanged.next({
      eventName: 'Event 1 name',
      category: '',
      venueName: '',
      startDate: null,
      endDate: null
    });
    service.eventsChanged.subscribe((responseData: IEventPage) => {
      eventPage = responseData;
    });
    service.getEvents();

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/search?page=0&size=8`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBe(searchParams);
    request.flush({
      events: [
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
        }
      ],
      totalSize: 2
    });

  }));

  it('should get event with given id', () => {
    const event = {
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
    };

    let resultEvent: IEventModel;
    service.getEvent(event.id).subscribe((responseData: IEventModel) => {
      resultEvent = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/` + event.id);
    expect(request.request.method).toBe('GET');
    request.flush(event);
    expect(resultEvent).toBeDefined();
    expect(resultEvent.id).toEqual(event.id);
    expect(resultEvent.name).toEqual(event.name);
    expect(resultEvent.venueId).toEqual(event.venueId);
    expect(resultEvent.venueName).toEqual(event.venueName);
    expect(resultEvent.description).toEqual(event.description);
    expect(resultEvent.reservationLimit).toEqual(event.reservationLimit);
  });

  it('should cancel event with given id', fakeAsync(() => {
    const event = {
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
    };

    let returnedEvent;
    service.cancelEvent(event.id).subscribe((responseData: IEventModel) => {
      returnedEvent = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/cancel/` + event.id);
    expect(request.request.method).toBe('GET');
    request.flush(event);
    tick();
    expect(returnedEvent).toBeDefined();
    expect(returnedEvent.id).toEqual(event.id);
    expect(returnedEvent.name).toEqual(event.name);
    expect(returnedEvent.venueId).toEqual(event.venueId);
    expect(returnedEvent.venueName).toEqual(event.venueName);
    expect(returnedEvent.description).toEqual(event.description);
    expect(returnedEvent.reservationLimit).toEqual(event.reservationLimit);
  }));

  it('should get image of the event with the given id', fakeAsync(() => {
    const eventId = 1;
    const fileName = 'event/image/filename';
    const blob = new Blob(['k', 'a', 'c', 'a'], {type: 'image/jpeg'});

    let retBlob: Blob;
    service.getEventImage(eventId, fileName).subscribe((responseData: Blob) => {
      retBlob = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/${eventId}/media/${fileName}`);
    expect(request.request.method).toBe('GET');
    expect(request.request.responseType).toBe('blob');
    request.flush(blob);
    tick();
    expect(retBlob).toBeDefined();
    expect(retBlob.size).toEqual(4);
    expect(retBlob.type).toEqual('image/jpeg');
  }));

  it('should get media files of the event with the given id', fakeAsync(() => {
    const eventId = 1;

    let returnedMediaFiles: IMediaFile[];
    service.getEventMediaFiles(eventId).subscribe((responseData: IMediaFile[]) => {
      returnedMediaFiles = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/${eventId}/media`);
    expect(request.request.method).toBe('GET');
    request.flush([
      {id: 1, fileName: 'fileName1', mimeType: 'image/jpeg'},
      {id: 2, fileName: 'fileName2', mimeType: 'image/png'}
    ]);
    tick();
    expect(returnedMediaFiles).toBeDefined();
    expect(returnedMediaFiles.length).toEqual(2);
    expect(returnedMediaFiles[0].id).toEqual(1);
    expect(returnedMediaFiles[0].fileName).toEqual('fileName1');
    expect(returnedMediaFiles[0].mimeType).toEqual('image/jpeg');
    expect(returnedMediaFiles[1].id).toEqual(2);
    expect(returnedMediaFiles[1].fileName).toEqual('fileName2');
    expect(returnedMediaFiles[1].mimeType).toEqual('image/png');
  }));

  it('should post files for event with given id', fakeAsync(() => {
    const eventId = 1;
    const files: File[] = [
      new File(['file1'], 'file1', {type: 'image/jpeg'}),
      new File(['file1'], 'file2', {type: 'image/png'})
    ];

    // headers
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'odata=verbose');
    // body
    const fd = new FormData();
    Array.from(files).forEach((file: File) => {
      fd.append('mediaFiles', file, file.name);
    });

    let returnedEvent: IEventModel;
    service.uploadImages(files, eventId).subscribe((responseData: IEventModel) => {
      returnedEvent = responseData;
    });

    const request = httpMock.expectOne(`http://localhost:${PORT}/api/events/${eventId}/media`);
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(fd);
    request.flush({
      id: 1,
      name: 'Event name',
      mediaFiles: [
        {id: 1, fileName: 'fileName1', mimeType: 'image/jpeg'},
        {id: 2, fileName: 'fileName2', mimeType: 'image/png'}
      ]
    });

    tick();
    expect(returnedEvent).toBeDefined();
    expect(returnedEvent.id).toEqual(1);
    expect(returnedEvent.name).toEqual('Event name');
    expect(returnedEvent.mediaFiles.length).toEqual(2);
    expect(returnedEvent.mediaFiles[0].id).toEqual(1);
    expect(returnedEvent.mediaFiles[0].fileName).toEqual('fileName1');
    expect(returnedEvent.mediaFiles[0].mimeType).toEqual('image/jpeg');
    expect(returnedEvent.mediaFiles[1].id).toEqual(2);
    expect(returnedEvent.mediaFiles[1].fileName).toEqual('fileName2');
    expect(returnedEvent.mediaFiles[1].mimeType).toEqual('image/png');

  }));

});
