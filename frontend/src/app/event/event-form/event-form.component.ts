import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService } from 'src/app/services/event.service';
import { VenueService, IVenue } from 'src/app/services/venue.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.scss']
})
export class EventFormComponent implements OnInit, OnDestroy {

  activeVenues: IVenue[] = [];
  activeVenuesSubscription: Subscription;
  mediaFiles: any = [];
  newEventForm: FormGroup;
  error = '';
  minDate = new Date();

  constructor(private eventService: EventService, private venueService: VenueService) { }

  ngOnInit() {
    this.newEventForm = new FormGroup({
      eventName: new FormControl('', Validators.required),
      category: new FormControl('', Validators.required),
      startDate: new FormControl(null, Validators.required),
      endDate: new FormControl(null, Validators.required),
      reservationDeadline: new FormControl(null, Validators.required),
      reservationLimit: new FormControl(null, [Validators.required, Validators.min(0)]),
      description: new FormControl('', Validators.required)
    });
    this.activeVenuesSubscription = this.venueService.activeVenuesChanged.subscribe(
      (venues: IVenue[]) => {
        this.activeVenues = venues;
        console.log('Active venues: ', this.activeVenues);
      }
    );
    this.venueService.fetchActiveVenues();
  }

  onSubmit() {
    console.log(this.newEventForm.value);
  }

  get eventName() {
    return this.newEventForm.get('eventName');
  }

  get category() {
    return this.newEventForm.get('category');
  }

  get startDateInfo() {
    return this.newEventForm.get('startDate');
  }

  get endDateInfo() {
    return this.newEventForm.get('endDate');
  }

  get reservationDeadlineInfo() {
    return this.newEventForm.get('reservationDeadline');
  }

  get reservationLimit() {
    return this.newEventForm.get('reservationLimit');
  }

  get description() {
    return this.newEventForm.get('description');
  }

  ngOnDestroy() {
    this.activeVenuesSubscription.unsubscribe();
  }

}
