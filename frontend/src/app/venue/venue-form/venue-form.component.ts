import {Component, Input, OnInit} from '@angular/core';
import {IVenue, VenueService} from '../../services/venue.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-venue-form',
  templateUrl: './venue-form.component.html',
  styleUrls: ['./venue-form.component.scss']
})
export class VenueFormComponent implements OnInit {
  venue: IVenue;
  new: boolean;
  found: boolean;

  constructor(private venueService: VenueService, private route: ActivatedRoute) { }

  ngOnInit() {
    const id = this.route.snapshot.params.id;
    console.log(id);
    this.found = false;
    if (id != null) {
      this.venueService.find(id).subscribe(resData => {
        this.venue = resData;
        this.found = true;
    });
      this.new = false;
    } else {
        this.new = true;
        this.found = true;
      }
  }

}
