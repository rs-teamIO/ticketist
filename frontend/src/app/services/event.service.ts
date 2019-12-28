import {Injectable} from '@angular/core';
import {EventModel} from '../model/event.model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Page} from '../model/page.model';

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

@Injectable({providedIn: 'root'})
export class EventService {
  eventsChanged = new Subject<IEventPage>();
  private readonly getEventsPath = 'http://localhost:8080/api/events/paged';
  private readonly searchEventsPath = 'http://localhost:8080/api/events/search';

  constructor(private http: HttpClient) { }

  getAll(page: Page): Observable<IEventPage> {
    const params: HttpParams = new HttpParams().set('page', String(page.page)).set('size', String(page.size));
    return this.http.get<IEventPage>(this.getEventsPath, {params});
  }

  searchAll(page: Page, searchParams: ISearchParams): void {
    const params: HttpParams = new HttpParams().set('page', String(page.page)).set('size', String(page.size));
    this.http.post<IEventPage>(this.searchEventsPath, searchParams, {params})
      .subscribe(responseData => {
        this.eventsChanged.next(responseData);
      });
  }

}
