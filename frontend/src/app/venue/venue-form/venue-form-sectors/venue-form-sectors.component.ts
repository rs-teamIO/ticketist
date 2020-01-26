import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {GridsterItem, GridsterConfig} from 'angular-gridster2';
import {FormGroup, FormControl, Validators, FormGroupDirective} from '@angular/forms';

@Component({
  selector: 'app-venue-form-sectors',
  templateUrl: './venue-form-sectors.component.html',
  styleUrls: ['./venue-form-sectors.component.scss']
})
export class VenueFormSectorsComponent implements OnInit {
  items: GridsterItem[] = [];
  options: GridsterConfig;

  addSectorForm: FormGroup;

  constructor() {
  }

  ngOnInit() {
    this.addSectorForm = new FormGroup({
      sectorName: new FormControl('', [Validators.required]),
      maxCapacity: new FormControl('', [Validators.required, Validators.min(1)])
    });

    this.options = {
      pushItems: true,
      gridType: 'fit',
      resizable: {
        enabled: true
      },
      draggable: {
        enabled: true
      },
      setGridSize: true,
      minCols: 50,
      maxCols: 500,
      minRows: 50,
      maxRows: 500
    };
  }

  onSubmit(formDirective: FormGroupDirective) {
    if (this.addSectorForm.invalid) {
      return;
    }
    this.items.push({
      cols: 10, rows: 10, y: 0, x: 0, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000,
      name: this.addSectorForm.get('sectorName').value, capacity: this.addSectorForm.get('maxCapacity').value
    });

    formDirective.resetForm();
    this.addSectorForm.reset();
  }

  onRemoveItem(item: any) {
    setTimeout(() => {
      this.items.splice(this.items.indexOf(item), 1);
    }, 0);
  }

  get sectorName() {
    return this.addSectorForm.get('sectorName');
  }

  get maxCapacity() {
    return this.addSectorForm.get('maxCapacity');
  }

}
