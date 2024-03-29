import {Injectable} from '@angular/core';
import {IEventModel} from '../model/event.model';
import {HttpClient, HttpParams, HttpHeaders} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Page} from '../model/page.model';
import {PageEvent} from '@angular/material';
import {PORT} from '../shared/constants';

export interface IEventPage {
  events: IEventModel[];
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
  id?: number;
  sectorId: number;
  ticketPrice: number;
  numeratedSeats: boolean;
  capacity?: number;
  date?: number;
}

export interface IMediaFile {
  id?: number;
  fileName?: string;
  mimeType?: string;
}

@Injectable({providedIn: 'root'})
export class EventService {
  private page: Page = new Page(0, 8);
  eventsChanged = new Subject<IEventPage>();
  pageChanged = new Subject<PageEvent>();
  searchParamsChanged = new Subject<ISearchParams>();

  private readonly getEventsPath = `http://localhost:${PORT}/api/events/paged`;
  private readonly getEventPath = `http://localhost:${PORT}/api/events/`;
  private readonly searchEventsPath = `http://localhost:${PORT}/api/events/search`;
  private readonly postEventPath = `http://localhost:${PORT}/api/events`;
  private readonly cancelEventPath = `http://localhost:${PORT}/api/events/cancel/`;

  private _searchParams = {
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
      this._searchParams = value;
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
    if (EventService.checkIfEmpty(this._searchParams)) {
      this.getAll();
    } else {
      this.searchAll();
    }
  }

  protected getAll(): void {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.get<IEventPage>(this.getEventsPath, {params})
      .subscribe(responseData => {
        this.eventsChanged.next(responseData);
      });
  }

  protected searchAll(): void {
    const params: HttpParams = new HttpParams().set('page', String(this.page.page)).set('size', String(this.page.size));
    this.http.post<IEventPage>(this.searchEventsPath, this._searchParams, {params})
      .subscribe(responseData => {
        this.eventsChanged.next(responseData);
      });
  }

  createEvent(event: IEvent) {
    const {eventSectors, mediaFiles, basicInfo} = event;
    return this.http.post<any>(
      this.postEventPath,
      {
        ...basicInfo,
        eventSectors,
        mediaFiles
      }
    );
  }

  uploadImages(selectedFiles: File[], eventID: number) {
    const fd = new FormData();

    Array.from(selectedFiles).forEach((file: File) => {
      fd.append('mediaFiles', file, file.name);
    });

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'odata=verbose');

    return this.http.post(`http://localhost:${PORT}/api/events/${eventID}/media`, fd, {headers});
  }

  getEvent(id: number): Observable<IEventModel> {
    return this.http.get<IEventModel>(this.getEventPath + id);
  }

  cancelEvent(id: number): Observable<IEventModel> {
    return this.http.get<IEventModel>(this.cancelEventPath + id);
  }

  getEventImage(eventId: number, fileName: string): Observable<Blob> {
    return this.http.get(`http://localhost:${PORT}/api/events/${eventId}/media/${fileName}`, {responseType: 'blob'});
  }

  getEventMediaFiles(eventId: number): Observable<IMediaFile[]> {
    return this.http.get<IMediaFile[]>(`http://localhost:${PORT}/api/events/${eventId}/media`);
  }

  /*
   * Util method for image converting (Blob to base64 string)
   */
  createImageFromBlob(image: Blob, onLoadCallback: any) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      onLoadCallback(reader.result);
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

}
