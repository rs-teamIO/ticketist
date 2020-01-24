import {Injectable} from '@angular/core';
import {EventModel} from '../model/event.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Page} from '../model/page.model';
import {PageEvent} from '@angular/material';
import { PORT } from '../shared/constants';
import {Venue} from '../model/venue.model';

export interface IEventPage {
  events: EventModel[];
  totalSize: number;
}

export interface ISearchParams {
  eventName: string;
  category: string;
  venueName: string;
  startDate: Date;
  endDate: Date;
}

export interface IEvent {
  basicInfo: IEventBasic;
  eventSectors: IEventSector[];
  mediaFiles: any[];
}

export interface IEventBasic {
  venueId: number;
  name: string;
  category: string;
  startDate: number;
  endDate: number;
  reservationDeadline: number;
  description: string;
  reservationLimit: number;
}

export interface IEventSector {
  sectorId: number;
  ticketPrice: number;
  numeratedSeats: boolean;
  capacity?: number;
}

@Injectable({providedIn: 'root'})
export class EventService {

  private page: Page = new Page(0, 8);
  eventsChanged = new Subject<IEventPage>();
  pageChanged = new Subject<PageEvent>();
  searchParamsChanged = new Subject<ISearchParams>();

  private readonly getEventsPath = `http://localhost:${PORT}/api/events/paged`;
  private readonly searchEventsPath = `http://localhost:${PORT}/api/events/search`;
  private readonly postEventPath = `http://localhost:${PORT}/api/events`;

  private searchParams = {
    eventName: '',
    category: '',
    venueName: '',
    startDate: null,
    endDate: null
  };

  constructor(private http: HttpClient) {
    this.pageChanged.subscribe((value) => {
      this.page = {page: value.pageIndex, size: value.pageSize};
    });
    this.searchParamsChanged.subscribe((value) => {
      this.searchParams = value;
    });
  }

  private static checkIfEmpty(searchParams: ISearchParams) {
    if (searchParams.eventName === '' && searchParams.category === '' && searchParams.venueName === '' &&
      searchParams.startDate === null && searchParams.endDate === null) {
      return true;
    }
    return false;
  }

  getEvents() {
    if (EventService.checkIfEmpty(this.searchParams)) {
      this.getAll();
    } else {
      this.searchAll();
    }
  }

  private getAll(): void {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.get<IEventPage>(this.getEventsPath, {params})
      .subscribe(responseData => {
        this.eventsChanged.next(responseData);
      });
  }

  private searchAll(): void {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.post<IEventPage>(this.searchEventsPath, this.searchParams, {params})
      .subscribe(responseData => {
        this.eventsChanged.next(responseData);
      });
  }

  createEvent(event: IEvent) {
    const { eventSectors, mediaFiles, basicInfo } = event;
    return this.http.post<any>(
      this.postEventPath,
      {
        ...basicInfo,
        eventSectors,
        mediaFiles
      }
    );
  }

  getEvent(id: number): Observable<EventModel> {
    return this.http.get<EventModel>(`http://localhost:${PORT}/api/events/` + id);
  }

  getVenue(id: number): Observable<Venue> {
    return this.http.get<Venue>(`http://localhost:${PORT}/api/venues/` + id);
  }

}
