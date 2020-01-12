import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Venue, VenueBasic, VenueService} from '../../../services/venue.service';
import {Sector} from '../../../services/sector.service';
import {GeocodeService} from '../../../services/geocode';

declare var google: any;

@Component({
  selector: 'app-venue-form-basic',
  templateUrl: './venue-form-basic.component.html',
  styleUrls: ['./venue-form-basic.component.scss']
})
export class VenueFormBasicComponent implements OnInit {
  @Input()
  new: boolean;
  @Input()
  venue: Venue;
  venueForm: FormGroup;
  isLoading = false;
  error = '';
  sectors: Sector[];
  sectorNew: Sector;



  constructor(private venueService: VenueService, private geocodeService: GeocodeService) { }

  ngOnInit() {
    this.venueForm = new FormGroup({
      name: new FormControl(null, Validators.required),
      city: new FormControl(null, Validators.required),
      street: new FormControl(null, Validators.required),
    });
    if (!this.new) {
      this.presetForm(this.venue);
    }
    console.log(this.venue);
  }

  private presetForm(resData: Venue) {
      this.venueForm = new FormGroup({
        name: new FormControl(resData.name, Validators.required),
        city: new FormControl(resData.city, Validators.required),
        street: new FormControl(resData.street, Validators.required)
      });
    }

  onSubmit() {
    if (!this.venueForm.valid) {
      this.error = 'Wrong inputs!';
      return;
    }

    this.isLoading = true;

    const { name, city, street } = this.venueForm.value;

    let latitude = 0.0;
    let longitude = 0.0;

    this.geocodeService.gelocate(city + ',' + street).subscribe(retData => {
    console.log(retData);

    latitude = retData.results[0].geometry.location.lat;
    longitude = retData.results[0].geometry.location.lng;

    const isActive = true;
    const id = null;
    const startRow = 5;
    const startColumn = 5;
    const rowsCount = 5;
    const columnsCount = 5;
    const maxCapacity = 5;

    const sectors: Sector[] = [{
      id,
      startRow,
      startColumn,
      columnsCount,
      rowsCount,
      name,
      maxCapacity
    }];

    this.error = '';
    if (this.new) {
      const venue: Venue = {
        id,
        name,
        city,
        street,
        latitude,
        longitude,
        isActive,
        sectors
      };

      this.venueService.create(venue).subscribe(
        resData => {
          console.log(resData);
          this.venue = resData;
          this.isLoading = false;
        },
        error => {
          console.log('Error: ', error);
          this.error = 'An error occured!';
          this.isLoading = false;
        }
      );

    } else {
      const venue: VenueBasic = {
        name,
        city,
        street,
        latitude,
        longitude,
      };
      this.venueService.update(this.venue.id, venue).subscribe(
        resData => {
          this.venue = resData;
          console.log(resData);
          this.isLoading = false;
        },
        error => {
          console.log('Error: ', error);
          this.error = 'An error occured!';
          this.isLoading = false;
        }
      );
    }



  });

  }

}
