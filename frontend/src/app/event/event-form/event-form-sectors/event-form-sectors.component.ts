import { Component, OnInit, OnDestroy } from '@angular/core';
import { IVenue, VenueService } from 'src/app/services/venue.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-event-form-sectors',
  templateUrl: './event-form-sectors.component.html',
  styleUrls: ['./event-form-sectors.component.scss']
})
export class EventFormSectorsComponent implements OnInit, OnDestroy {

  activeVenues: IVenue[] = [];
  subscription: Subscription;

  constructor(private venueService: VenueService) { }

  ngOnInit() {
    this.subscription = this.venueService.activeVenuesChanged.subscribe(
      (venues: IVenue[]) => {
        this.activeVenues = venues;
        console.log('Active venues: ', this.activeVenues);
      }
    );
    this.venueService.fetchActiveVenues();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}
