import {Injectable} from '@angular/core';
import {Dogadjaj} from '../model/event.model';

@Injectable({providedIn: 'root'})
export class EventService {
  events: Dogadjaj[] = [
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28)),
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28)),
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28)),
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28)),
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28)),
    new Dogadjaj('Event 1', 'Venue name', new Date(2019, 12, 28))
  ];

  getEvents() {
    return this.events;
  }
}
