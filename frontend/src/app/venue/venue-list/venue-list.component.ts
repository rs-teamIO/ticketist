import { Component, OnInit } from '@angular/core';
import {Venue, VenueService} from '../../services/venue.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-venue-list',
  templateUrl: './venue-list.component.html',
  styleUrls: ['./venue-list.component.scss']
})
export class VenueListComponent implements OnInit {
  venues: Venue[];

  constructor(private venueService: VenueService, private router: Router) { }

  ngOnInit() {
      this.venueService.retrieve().subscribe(
        resData => {
          this.venues = resData;
        }, error => {
          console.log('Error: ', error);
        }
      );
  }

  activate(id: string) {
      this.venueService.activate(id).subscribe( resData => {
      console.log(resData);
      });
  }
}
