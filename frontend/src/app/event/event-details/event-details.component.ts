import {Component, OnInit} from '@angular/core';
import {EventModel} from '../../model/event.model';
import {EventService} from '../../services/event.service';
import {VenueService} from '../../services/venue.service';
import {Venue} from '../../model/venue.model';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit {

  event: EventModel = new EventModel();
  venue: Venue = new Venue();
  imageToShow: any;
  imageLoading = true;

  constructor(private eventService: EventService,
              private venueService: VenueService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.eventService.getEvent(this.route.snapshot.params.id)
      .subscribe(eventModel => {
        this.event = eventModel;

        if (this.event.mediaFiles.length !== 0) {
          this.eventService.getEventImage(this.event.id, this.event.mediaFiles[0].fileName).subscribe((response: Blob) => {
            this.imageToShow = this.createImageFromBlob(response);
            this.imageLoading = false;
          });
        }

        this.venueService.getVenue(this.event.venueId)
          .subscribe(responseData => {
            this.venue = responseData;
          });
      });
  }

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow =  reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

}
