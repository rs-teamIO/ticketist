import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Report} from '../model/report.model';
import {Observable, Subject} from 'rxjs';
import {IEventPage} from './event.service';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly getInitialReportPath = 'http://localhost:8080/api/reports';
  initialReportChanged = new Subject<Report>();
  specificReportChanged = new Subject<Report>();
  eventsListChanged = new Subject<any[]>();

  constructor(private http: HttpClient) {
  }

  getInitialReport(): void {
    this.http.get<Report>(this.getInitialReportPath)
      .subscribe((r: Report) => {
        this.initialReportChanged.next(r);
        this.eventsListChanged.next(r.events);
      });
  }

  getSpecificReport(venueName: string, criteria: string): void {
    this.http.get<Report>(this.getInitialReportPath + '/' + venueName + '/' + criteria)
      .subscribe((r: Report) => {
        this.specificReportChanged.next(r);
        this.eventsListChanged.next(r.events);
      });
  }

}

