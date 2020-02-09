import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatSort, MatTableDataSource} from '@angular/material';
import {ReportService} from '../../services/report.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-report-table',
  templateUrl: './report-table.component.html',
  styleUrls: ['./report-table.component.scss']
})
export class ReportTableComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['name', 'venueName', 'ticketsSold', 'totalRevenue'];
  dataSource: MatTableDataSource<any>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  subscription: Subscription;

  ngOnInit() {
    this.subscription = this.reportService.eventsListChanged
      .subscribe((events: any[]) => {
        this.dataSource = new MatTableDataSource(events);
        this.dataSource.sort = this.sort;
      });
  }

  constructor(private reportService: ReportService) {
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
