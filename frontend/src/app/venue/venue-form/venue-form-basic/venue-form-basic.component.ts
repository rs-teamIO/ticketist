import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Venue, VenueService} from '../../../services/venue.service';
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
  latitude: number;
  longitude: number;



  constructor(private venueService: VenueService, private geocodeService: GeocodeService) { }

  ngOnInit() {
    this.venueForm = new FormGroup({
      name: new FormControl(null, Validators.required),
      city: new FormControl(null, Validators.required),
      street: new FormControl(null, Validators.required),
    });
    if (!this.new) {
      this.presetForm(this.venue);
      this.latitude = this.venue.latitude;
      this.longitude = this.venue.longitude;
    }
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
      latitude = retData[0].geometry.location.lat();
      longitude = retData[0].geometry.location.lng();

      let isActive = true;
      let id = '0';

      if (!this.new) {
        isActive = this.venue.isActive;
        id = this.venue.id;
      }

      const sectors = this.sectors;

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

      this.error = '';

      this.venueService.update(venue).subscribe(
        resData => {
          console.log(resData);
          this.isLoading = false;
        },
        error => {
          console.log('Error: ', error);
          this.error = 'An error occured!';
          this.isLoading = false;
        }
      );
    });

  }

}
