import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'toInitialChart'
})
export class ToInitialChartPipe implements PipeTransform {

  transform(allVenuesChart: { [x: string]: number }): any {
    const arr = [];
    Object.keys(allVenuesChart).map(venueName => {
      const ret = {name: venueName, value: allVenuesChart[venueName]};
      arr.push(ret);
    });
    return arr;
  }

}
