import {Component, OnInit} from '@angular/core';
import {IEventModel} from '../../model/event.model';
import {EventService} from '../../services/event.service';
import {IVenue, VenueService} from '../../services/venue.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit {

  event: IEventModel = {} as IEventModel;
  venue: IVenue = {} as IVenue;
  imageToShow: any;
  imageLoading = true;

  constructor(private eventService: EventService,
              private venueService: VenueService,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.eventService.getEvent(this.route.snapshot.params.id)
      .subscribe(eventModel => {
        this.event = eventModel;

        if (this.event.mediaFiles.length > 0) {
          this.eventService.getEventImage(this.event.id, this.event.mediaFiles[0].fileName).subscribe((response: Blob) => {
            this.eventService.createImageFromBlob(response, (readerResponse) => {
              this.imageToShow = readerResponse;
              this.imageLoading = false;
            });
          });
        }

        this.venueService.getVenue(this.event.venueId)
          .subscribe(responseData => {
            this.venue = responseData;
          });
      });
  }

}
