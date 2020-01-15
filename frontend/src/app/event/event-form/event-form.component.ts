import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService, IEventSector } from 'src/app/services/event.service';
import { VenueService, IVenue, ISector } from 'src/app/services/venue.service';
import { IEvent } from 'src/app/services/event.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';

interface ISectorTable {
  sectorName: string;
  maxCapacity: number;
  ticketPrice: number;
  capacity: number;
  numeratedSeats: boolean;
  active: boolean;
}

@Component({
  selector: 'app-event-form',
  templateUrl: './event-form.component.html',
  styleUrls: ['./event-form.component.scss']
})
export class EventFormComponent implements OnInit, OnDestroy {

  minDate = new Date(new Date().getTime() + 1000 * 60 * 60 * 24);
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
      reservationDeadline: new FormControl(null, [Validators.required, this.reservationDeadlineValidator.bind(this)]),
      startDate: new FormControl(null, [Validators.required, this.startDateValidator.bind(this)]),
      endDate: new FormControl(null, [Validators.required, this.endDateValidator.bind(this)]),
      reservationLimit: new FormControl(null, [Validators.required, Validators.min(1)]),
      description: new FormControl('', Validators.required)
    });

    this.sectorForm = new FormGroup({
      venueName: new FormControl('', Validators.required),
      sectors: new FormArray([])
    });

    this.sectorForm.get('venueName').valueChanges.subscribe((value: any) => {
      // console.log('1: ', this.reservationDeadlineInfo.errors);
      // console.log('2: ', this.startDateInfo.errors);
      // console.log('3: ', this.endDateInfo.errors);
      this.activeVenues.forEach((venue: IVenue) => {
        if (venue.name === value) {
          this.currentVenue = venue;
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
  }

  onSubmit() {
    let eventSectors: IEventSector[] = this.sectorForm.controls.sectors.value.map((sector: ISectorTable, index: number) => {
      if (sector.active) {
        const newEventSector: IEventSector = {
          sectorId: this.currentVenue.sectors[index].id,
          ticketPrice: sector.ticketPrice,
          numeratedSeats: sector.numeratedSeats
        };
        if (!sector.numeratedSeats) {
          newEventSector.capacity = sector.capacity;
        }
        return newEventSector;
      } else {
        return null;
      }
    });
    eventSectors = eventSectors.filter((sector: IEventSector) => sector !== null);

    // change later
    if (eventSectors.length === 0) {
      return;
    }

    const newEvent: IEvent = {
      basicInfo: {
        venueId: this.currentVenue.id,
        name: this.eventName.value,
        category: this.category.value,
        startDate: new Date(this.startDateInfo.value).getTime(),
        endDate: new Date(this.endDateInfo.value).getTime(),
        reservationDeadline: new Date(this.reservationDeadlineInfo.value).getTime(),
        description: this.description.value,
        reservationLimit: this.reservationLimit.value
      },
      eventSectors,
      mediaFiles: []
    };
    this.eventService.createEvent(newEvent);
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

  reservationDeadlineValidator(control: FormControl): {[s: string]: boolean} {
    if (!this.newEventForm || !control.value) {
      return null;
    } else if (this.startDateInfo.value &&
      new Date(control.value).getTime() > new Date(this.startDateInfo.value).getTime()) {
      return { reservationDeadlineAfterStartDate: true };
    } else if (this.endDateInfo.value &&
      new Date(control.value).getTime() > new Date(this.endDateInfo.value).getTime()) {
      return { reservationDeadlineAfterEndDate: true };
    }
    return null;
  }

  startDateValidator(control: FormControl): {[s: string]: boolean} {
    if (!this.newEventForm || !control.value) {
      return null;
    } else if (this.reservationDeadlineInfo.value &&
      new Date(this.reservationDeadlineInfo.value).getTime() > new Date(control.value).getTime()) {
      return { startDateBeforeDeadline: true };
    } else if (this.endDateInfo.value &&
      new Date(control.value).getTime() > new Date(this.endDateInfo.value).getTime()) {
      return { startDateAfterEndDate: true };
    }
    return null;
  }

  endDateValidator(control: FormControl): {[s: string]: boolean} {
    if (!this.newEventForm || !control.value) {
      return null;
    } else if (this.reservationDeadlineInfo.value &&
      new Date(this.reservationDeadlineInfo.value).getTime() > new Date(control.value).getTime()) {
      return { endDateBeforeDeadline: true };
    } else if (this.startDateInfo.value &&
      new Date(this.startDateInfo.value).getTime() > new Date(control.value).getTime()) {
      return { endDateBeforeStartDate: true };
    }
    return null;
  }

  ngOnDestroy() {
    this.activeVenuesSubscription.unsubscribe();
  }

}
