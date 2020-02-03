import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-map-view',
  templateUrl: './map-view.component.html',
  styleUrls: ['./map-view.component.scss']
})
export class MapViewComponent implements OnInit {
  @Input()
  latitude: number;
  @Input()
  longitude: number;

  constructor() { }

  ngOnInit() {
  }

}
