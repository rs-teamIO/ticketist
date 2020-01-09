import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Report} from '../model/report.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly getInitialReportPath = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {
  }

  getInitialReport(): Observable<Report> {
    return this.http.get<Report>(this.getInitialReportPath);
  }
}

