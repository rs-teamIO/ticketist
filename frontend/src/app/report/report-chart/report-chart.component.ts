import {Component, OnInit} from '@angular/core';
import {ReportService} from '../../services/report.service';
import {map} from 'rxjs/operators';
import {Report} from '../../model/report.model';

@Component({
  selector: 'app-report-chart',
  templateUrl: './report-chart.component.html',
  styleUrls: ['./report-chart.component.scss']
})
export class ReportChartComponent implements OnInit {
  allVenuesChart: { [x: string]: number }[] = [];

  // options
  showXAxis = true;
  showYAxis = true;
  yAxisLabel = 'Revenue ($)';

  colorScheme = {
    domain: ['#673AB7', '#ed8a0a', '#4527A0', '#f6d912', '#fff29c']
  };

  ngOnInit(): void {
    this.reportService.getInitialReport()
      .pipe(map((r: Report) => r.allVenuesChart),
        map((nzm: { [x: string]: number }) => {
          const arr = [];
          Object.keys(nzm).map(e => {
            const ret = {name: e, value: nzm[e]};
            arr.push(ret);
          });
          return arr;
        }))
      .subscribe(chartData => {
        this.allVenuesChart = chartData;
      });


  }

  constructor(private reportService: ReportService) {
  }
}
