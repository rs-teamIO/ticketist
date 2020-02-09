import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'toMonthChart'
})
export class ToMonthChartPipe implements PipeTransform {

  transform(singleVenueChart: { [x: number]: number }, selectedVenue: string): any {
    const chart = {name: selectedVenue, series: []};
    for (let i = 1; i < 13; i++) {
      chart.series[i - 1] = singleVenueChart.hasOwnProperty(i) ? {
        name: i,
        value: singleVenueChart[i]
      } : {name: i, value: 0};
    }
    return chart;
  }

}
