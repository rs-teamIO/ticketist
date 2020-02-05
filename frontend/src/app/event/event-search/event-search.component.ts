import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {EventService, ISearchParams} from '../../services/event.service';
import {Page} from '../../model/page.model';
import {EventModel} from '../../model/event.model';
import {VenueService} from '../../services/venue.service';

@Component({
  selector: 'app-event-search',
  templateUrl: './event-search.component.html',
  styleUrls: ['./event-search.component.scss']
})
export class EventSearchComponent implements OnInit {
  searchForm: FormGroup;
  venueNames: string[] = [];

  constructor(private eventService: EventService,
              private venueService: VenueService) { }

  ngOnInit() {
    this.searchForm = new FormGroup({
      eventName: new FormControl(''),
      category: new FormControl(''),
      venueName: new FormControl(''),
      startDate: new FormControl(null),
      endDate: new FormControl(null),
    });

    this.venueService.retrieve().subscribe(response => {
      this.venueNames = response.map(venue => venue.name);
    });
  }

  onSubmit() {
    const searchParams: ISearchParams = {
      eventName: this.searchForm.get('eventName').value,
      category: this.searchForm.get('category').value,
      venueName: this.searchForm.get('venueName').value,
      startDate: this.searchForm.get('startDate').value !== null ? this.searchForm.get('startDate').value.getTime() : null,
      endDate: this.searchForm.get('endDate').value !== null ? this.searchForm.get('endDate').value.getTime() : null
    };

    this.eventService.searchParamsChanged.next(searchParams);
    this.eventService.getEvents();
  }


}
