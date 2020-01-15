import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService } from 'src/app/services/event.service';
import { VenueService, IVenue, ISector } from 'src/app/services/venue.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.scss']
})
export class EventFormComponent implements OnInit, OnDestroy {

  minDate = new Date();
  activeVenues: IVenue[] = [];
  currentVenue: IVenue = null;
  activeVenuesSubscription: Subscription;
  mediaFiles: any = [];
  error = '';
  newEventForm: FormGroup;
  sectorForm: FormGroup;

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

    this.sectorForm = new FormGroup({
      venueName: new FormControl('', Validators.required),
      sectors: new FormArray([])
    });

    this.sectorForm.get('venueName').valueChanges.subscribe((value: any) => {
      this.activeVenues.forEach((venue: IVenue) => {
        if (venue.name === value) {
          this.currentVenue = venue;
          console.log('Current venue: ', this.currentVenue);
          this.onVenueChanged();
        }
      });
    });

    this.activeVenuesSubscription = this.venueService.activeVenuesChanged.subscribe(
      (venues: IVenue[]) => {
        this.activeVenues = venues;
        if (this.activeVenues.length > 0) {
          this.sectorForm.patchValue({
            venueName: this.activeVenues[0].name
          });
        }
      }
    );

    this.venueService.fetchActiveVenues();
  }

  onVenueChanged() {
    (this.sectorForm.get('sectors') as FormArray).clear();
    this.currentVenue.sectors.forEach((sector: ISector) => {
      (this.sectorForm.get('sectors') as FormArray).push(
        new FormGroup({
          sectorName: new FormControl(sector.name),
          maxCapacity: new FormControl(sector.maxCapacity),
          ticketPrice: new FormControl(0, Validators.required),
          capacity: new FormControl(0, Validators.required),
          numeratedSeats: new FormControl(true, Validators.required),
          active: new FormControl(false, Validators.required)
        })
      );
    });
    console.log('>>>> ', this.sectorForm.controls);
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
