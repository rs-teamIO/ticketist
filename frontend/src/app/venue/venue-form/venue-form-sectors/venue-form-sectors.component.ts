import { Component, OnInit } from '@angular/core';
import { GridsterItem, GridsterConfig } from 'angular-gridster2';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-venue-form-sectors',
  templateUrl: './venue-form-sectors.component.html',
  styleUrls: ['./venue-form-sectors.component.scss']
})
export class VenueFormSectorsComponent implements OnInit {
  sectors: any[] = [
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},
    {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H'},

  ];
  displayedColumns: string[] = ['Sector name', 'Rows', 'Seats per row', 'Capacity'];

  items: GridsterItem[] = [
    // {cols: 10, rows: 10, y: 5, x: 5, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000},
    // {cols: 10, rows: 10, y: 5, x: 5, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000},
    // {cols: 10, rows: 10, y: 5, x: 5, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000}
  ];
  options: GridsterConfig;

  addSectorForm: FormGroup;

  constructor() { }

  ngOnInit() {
    this.addSectorForm = new FormGroup({
      sectorName: new FormControl(),
      maxCapacity: new FormControl()
    });
    
    this.options = {
      // itemChangeCallback: SeatsDisplayComponent.itemChange,
      // itemResizeCallback: SeatsDisplayComponent.itemResize,
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

  onSubmit() {
    this.items.push({
      cols: 10, rows: 10, y: 0, x: 0, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000, 
      name: this.addSectorForm.get('sectorName').value, capacity: this.addSectorForm.get('maxCapacity').value
    })
    this.addSectorForm.reset();
  }

  onRemoveItem(item: any) {
    setTimeout(() => {
      this.items.splice(this.items.indexOf(item), 1);
    }, 0);
  }

}
