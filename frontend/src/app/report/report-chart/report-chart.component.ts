import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ReportService} from '../../services/report.service';
import {map} from 'rxjs/operators';
import {Report} from '../../model/report.model';
import {ToMonthChartPipe} from '../../pipes/to-month-chart.pipe';
import {Subscription} from 'rxjs';
import {ToInitialChartPipe} from '../../pipes/to-initial-chart.pipe';

@Component({
  selector: 'app-report-chart',
  templateUrl: './report-chart.component.html',
  styleUrls: ['./report-chart.component.scss'],
  providers: [ToMonthChartPipe, ToInitialChartPipe]
})
export class ReportChartComponent implements OnInit, OnDestroy {
  allVenuesChart: { [x: string]: number }[] = [];
  singleVenueChart: { [x: number]: number }[] = [];

  selectedVenue = '';
  selectedCriteria = 'revenue';

  @ViewChild('criteriaRef', {static: false}) criteriaRef: any;
  yAxisLabel = 'Revenue ($)';
  colorScheme = {
    domain: ['#673AB7', '#ed8a0a', '#4527A0', '#f6d912', '#fff29c']
  };

  initialReportSubscription: Subscription;
  specificReportSubscription: Subscription;

  constructor(private reportService: ReportService,
              private toMonthChartPipe: ToMonthChartPipe,
              private toInitialChartPipe: ToInitialChartPipe) {
  }

  ngOnInit(): void {
    this.initialReportSubscription = this.reportService.initialReportChanged
      .pipe(map((report: Report) => {
        return this.toInitialChartPipe.transform(report.allVenuesChart);
      })).subscribe(chart => this.allVenuesChart = chart);

    this.specificReportSubscription = this.reportService.specificReportChanged
      .pipe(map((report: Report) => {
        return this.toMonthChartPipe.transform(report.singleVenueChart, this.selectedVenue);
      })).subscribe(chart => this.singleVenueChart = [chart]);

    this.reportService.getInitialReport();
  }

  onSelect(data): void {
    this.selectedVenue = JSON.parse(JSON.stringify(data));
    this.reportService.getSpecificReport(this.selectedVenue, this.selectedCriteria);
  }

  onCriteriaValueChange() {
    this.reportService.getSpecificReport(this.selectedVenue, this.selectedCriteria);
    this.yAxisLabel = this.criteriaRef.triggerValue;
  }

  onGetAllVenuesReport() {
    this.selectedVenue = '';
    this.yAxisLabel = 'Revenue ($)';
    this.reportService.getInitialReport();
  }

  ngOnDestroy(): void {
    this.initialReportSubscription.unsubscribe();
    this.specificReportSubscription.unsubscribe();
  }
}
