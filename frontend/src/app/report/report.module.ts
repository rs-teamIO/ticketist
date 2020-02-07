import {NgModule} from '@angular/core';
import { MatFormFieldModule, MatSelectModule, MatCardModule, MatTableModule, MatButtonModule } from '@angular/material';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { CommonModule } from '@angular/common';
import { ReportComponent } from './report.component';
import { ReportChartComponent } from './report-chart/report-chart.component';
import { ReportTableComponent } from './report-table/report-table.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
  imports: [
    CommonModule,
    FlexLayoutModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    NgxChartsModule,
    MatSelectModule,
    MatCardModule,
    MatTableModule,
    MatButtonModule
  ],
  declarations: [
    ReportComponent,
    ReportChartComponent,
    ReportTableComponent
  ],
  exports: [
    ReportComponent,
    ReportChartComponent,
    ReportTableComponent
  ],
})
export class ReportModule {}
