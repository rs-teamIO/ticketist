import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventService, IEventSector } from 'src/app/services/event.service';
import { VenueService, IVenue } from 'src/app/services/venue.service';
import { IEvent } from 'src/app/services/event.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { ISector } from 'src/app/services/sector.service';

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
  newEventForm: FormGroup;
  sectorForm: FormGroup;
  errorMessage = '';
  selectedFiles: File[] = [];

  constructor(private router: Router, private eventService: EventService, private venueService: VenueService) { }

  ngOnInit() {
    this.newEventForm = new FormGroup({
      eventName: new FormControl('', Validators.required),
      category: new FormControl('', Validators.required),
      dates: new FormGroup({
        reservationDeadline: new FormControl(null, [Validators.required]),
        startDate: new FormControl(null, [Validators.required]),
        endDate: new FormControl(null, [Validators.required])
      }, [this.datesValidator.bind(this)]),
      reservationLimit: new FormControl(null, [Validators.required, Validators.min(1)]),
      description: new FormControl('', Validators.required)
    });

    this.sectorForm = new FormGroup({
      venueName: new FormControl('', Validators.required),
      sectors: new FormArray([])
    }, this.checkAtLeastOneSectorValidator.bind(this));

    this.sectorForm.get('venueName').valueChanges.subscribe((value: any) => {
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
        }, this.sectorRowValidator.bind(this))
      );
    });
  }

  onSubmit() {
    if (this.newEventForm.invalid || this.sectorForm.invalid) {
      return;
    }

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

    const newEvent: IEvent = {
      basicInfo: {
        venueId: this.currentVenue.id,
        name: this.eventName.value,
        category: this.category.value,
        startDate: new Date(this.startDateInfo.value).getTime() + 60 * 60 * 1000,
        endDate: new Date(this.endDateInfo.value).getTime() + 2 * 60 * 60 * 1000,
        reservationDeadline: new Date(this.reservationDeadlineInfo.value).getTime(),
        description: this.description.value,
        reservationLimit: this.reservationLimit.value
      },
      eventSectors,
      mediaFiles: []
    };

    this.eventService.createEvent(newEvent)
    .subscribe((resData: any) => {
      console.log('RES: ', resData);
      if (this.selectedFiles.length > 0) {
        this.eventService.uploadImages(this.selectedFiles, resData.id)
        .subscribe((mediaData: any) => {
          this.sectorForm.reset();
          this.newEventForm.reset();
          this.errorMessage = '';
          this.router.navigate(['/events']);
        }, (error: any) => {
          this.errorMessage = error.status === 400 ? error.error.message : 'Error!';
        });
      } else {
        this.sectorForm.reset();
        this.newEventForm.reset();
        this.errorMessage = '';
        this.router.navigate(['/events']);
      }
    },
    (error: any) => {
      console.log('ERROR: ', error);
      if (error.status === 400) {
        this.errorMessage = error.error.message;
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFiles = (event.target.files as File[]);
  }

  get eventName() {
    return this.newEventForm.get('eventName');
  }

  get category() {
    return this.newEventForm.get('category');
  }

  get datesInfo() {
    return this.newEventForm.get('dates');
  }

  get startDateInfo() {
    return this.newEventForm.get('dates').get('startDate');
  }

  get endDateInfo() {
    return this.newEventForm.get('dates').get('endDate');
  }

  get reservationDeadlineInfo() {
    return this.newEventForm.get('dates').get('reservationDeadline');
  }

  get reservationLimit() {
    return this.newEventForm.get('reservationLimit');
  }

  get description() {
    return this.newEventForm.get('description');
  }

  datesValidator(group: FormGroup): {[s: string]: boolean} {
    if (!this.newEventForm) {
      return null;
    }

    // reservation deadline validator
    if (!group.controls.reservationDeadline.value) {
      return { reservationDeadlineRequired: true };
    } else if (this.startDateInfo.value &&
      new Date(group.controls.reservationDeadline.value).getTime() > new Date(this.startDateInfo.value).getTime()) {
      return { reservationDeadlineAfterStartDate: true };
    } else if (this.endDateInfo.value &&
      new Date(group.controls.reservationDeadline.value).getTime() > new Date(this.endDateInfo.value).getTime()) {
      return { reservationDeadlineAfterEndDate: true };
    }

    // start date validator
    if (!group.controls.startDate.value) {
      return { startDateRequired: true };
    } else if (this.reservationDeadlineInfo.value &&
      new Date(this.reservationDeadlineInfo.value).getTime() > new Date(group.controls.startDate.value).getTime()) {
      return { startDateBeforeDeadline: true };
    } else if (this.endDateInfo.value &&
      new Date(group.controls.startDate.value).getTime() > new Date(this.endDateInfo.value).getTime()) {
      return { startDateAfterEndDate: true };
    }

    // end date validator
    if (!group.controls.endDate.value) {
      return { endDateRequired: true };
    } else if (this.reservationDeadlineInfo.value &&
      new Date(this.reservationDeadlineInfo.value).getTime() > new Date(group.controls.endDate.value).getTime()) {
      return { endDateBeforeDeadline: true };
    } else if (this.startDateInfo.value &&
      new Date(this.startDateInfo.value).getTime() > new Date(group.controls.endDate.value).getTime()) {
      return { endDateBeforeStartDate: true };
    }

    return null;
  }

  sectorRowValidator(group: FormGroup): {[s: string]: boolean} {
    if (group.controls.active.value) {
      if (isNaN(group.controls.ticketPrice.value)) {
        return { priceIsNotNumber: true };
      } else if (group.controls.ticketPrice.value <= 0) {
        return { priceNotPositive: true };
      }
      if (!group.controls.numeratedSeats.value) {
        if (isNaN(group.controls.capacity.value)) {
          return { capacityIsNotNumber: true };
        } else if (group.controls.capacity.value <= 0) {
          return { capacityNotPositive: true };
        } else if (group.controls.capacity.value > group.controls.maxCapacity.value) {
          return { capacityGreaterThanMaxCapacity: true };
        }
      }
    }

    return null;
  }

  checkAtLeastOneSectorValidator(group: FormGroup): {[s: string]: boolean} {
    let checkExistanceOfSector = false;
    group.value.sectors.forEach((sector: ISectorTable) => {
      if (sector.active) {
        checkExistanceOfSector = true;
      }
    });
    if (checkExistanceOfSector) {
      return null;
    } else {
      return { noSelectedSectors: true };
    }
  }

  ngOnDestroy() {
    this.activeVenuesSubscription.unsubscribe();
  }

}
