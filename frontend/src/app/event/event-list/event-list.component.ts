import {Component, OnInit} from '@angular/core';
import {PageEvent} from '@angular/material';
import {Dogadjaj} from '../../model/event.model';
import {EventService} from '../../services/event.service';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit {
// MatPaginator Output
  pageEvent: PageEvent;
  events: Dogadjaj[];

  constructor(private eventService: EventService) { }

  ngOnInit() {
    this.events = this.eventService.getEvents();
  }

}
