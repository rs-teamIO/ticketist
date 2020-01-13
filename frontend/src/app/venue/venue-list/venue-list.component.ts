import {AfterViewInit, Component, OnInit, OnDestroy} from '@angular/core';
import {IVenuePage, Venue, VenueService} from '../../services/venue.service';
import {Router} from '@angular/router';
import {PageEvent} from '@angular/material/paginator';
import {Subscription} from 'rxjs';


@Component({
  selector: 'app-venue-list',
  templateUrl: './venue-list.component.html',
  styleUrls: ['./venue-list.component.scss']
})
export class VenueListComponent implements OnInit, AfterViewInit, OnDestroy {
  venues: Venue[];
  totalSize: number;
  subscription: Subscription;

  constructor(private venueService: VenueService, private router: Router) { }

  ngOnInit() {
    /*
    this.venueService.retrieve().subscribe(
        resData => {
          this.venues = resData;
        }, error => {
          console.log('Error: ', error);
        }
      );
     */
  }

  ngAfterViewInit(): void {
    this.subscription = this.venueService.venuesChanged
      .subscribe((eventPage: IVenuePage) => {
        this.venues = eventPage.venues;
        this.totalSize = eventPage.totalSize;
      });
    this.loadData();
  }

  onPageChanged(pageEvent: PageEvent) {
    this.venueService.pageChanged.next(pageEvent);
    this.loadData();
  }

  loadData() {
    this.venueService.venuesPaged();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
