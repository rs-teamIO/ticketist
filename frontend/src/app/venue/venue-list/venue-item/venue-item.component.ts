import {Component, Input, OnInit} from '@angular/core';
import {Venue} from '../../../services/venue.service';

@Component({
  selector: 'app-venue-item',
  templateUrl: './venue-item.component.html',
  styleUrls: ['./venue-item.component.scss']
})
export class VenueItemComponent implements OnInit {
  @Input()
  venue: Venue;
  sectorSize: number;

  constructor() { }

  ngOnInit() {
    this.sectorSize = 0;
    for (let i = 0; i < this.venue.sectors.length; i++) {
      this.sectorSize += this.venue.sectors[i].maxCapacity;
    }
  }

}
