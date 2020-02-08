import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Subject} from 'rxjs';
import {PORT} from '../shared/constants';

export interface IReport {
  allVenuesChart: { [x: string]: number};
  singleVenueChart: { [x: number]: number};
  events: any[];
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly getInitialReportPath = `http://localhost:${PORT}/api/reports`;
  initialReportChanged = new Subject<IReport>();
  specificReportChanged = new Subject<IReport>();
  eventsListChanged = new Subject<any[]>();

  constructor(private http: HttpClient) {
  }

  getInitialReport(): void {
    this.http.get<IReport>(this.getInitialReportPath)
      .subscribe((r: IReport) => {
        this.initialReportChanged.next(r);
        this.eventsListChanged.next(r.events);
      });
  }

  getSpecificReport(venueName: string, criteria: string): void {
    this.http.get<IReport>(this.getInitialReportPath + '/' + venueName + '/' + criteria)
      .subscribe((r: IReport) => {
        this.specificReportChanged.next(r);
        this.eventsListChanged.next(r.events);
      });
  }

}

