import { Component, OnInit } from '@angular/core';
import {EventModel} from '../../model/event.model';
import {EventService} from '../../services/event.service';
import {Venue} from '../../model/venue.model';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit {

  event: EventModel = new EventModel();
  venue: Venue = new Venue();

  constructor(private eventService: EventService) { }

  ngOnInit() {
    this.eventService.getEvent(12)
      .subscribe(eventModel => {
        this.event = eventModel;
        this.eventService.getVenue(this.event.venueId)
          .subscribe(responseData => {
            this.venue = responseData;
            // console.log(this.event);
          });
      });
  }


}
