import {Component, Input, OnInit} from '@angular/core';
import {IVenue, VenueService} from '../../../services/venue.service';
import {Router} from '@angular/router';
import { ISector } from 'src/app/services/sector.service';

@Component({
  selector: 'app-venue-item',
  templateUrl: './venue-item.component.html',
  styleUrls: ['./venue-item.component.scss']
})
export class VenueItemComponent implements OnInit {
  @Input()
  venue: IVenue;
  sectorSize: number;

  constructor(private venueService: VenueService, private router: Router) { }

  ngOnInit() {
    this.sectorSize = 0;
    this.venue.sectors.forEach((sector: ISector) => {
      this.sectorSize += sector.maxCapacity;
    });
  }


  activate(id: number) {
    this.venueService.activate(id).subscribe( resData => {

      this.venue.isActive = resData;

    });
  }

}
