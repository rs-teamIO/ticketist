import {Component, Input, OnInit} from '@angular/core';
import {Venue, VenueService} from '../../../services/venue.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-venue-item',
  templateUrl: './venue-item.component.html',
  styleUrls: ['./venue-item.component.scss']
})
export class VenueItemComponent implements OnInit {
  @Input()
  venue: Venue;
  sectorSize: number;

  constructor(private venueService: VenueService, private router: Router) { }

  ngOnInit() {
    this.sectorSize = 0;
    for (let i = 0; i < this.venue.sectors.length; i++) {
      this.sectorSize += this.venue.sectors[i].maxCapacity;
    }
  }


  activate(id: string) {
    this.venueService.activate(id).subscribe( resData => {

      this.venue.isActive = resData;

    });
  }

}
