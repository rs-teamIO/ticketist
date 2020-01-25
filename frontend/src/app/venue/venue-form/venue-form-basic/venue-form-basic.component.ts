import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {IVenue, IVenueBasic, VenueService} from '../../../services/venue.service';
import {ISector} from '../../../services/sector.service';
import {GeocodeService} from '../../../services/geocode.service';

@Component({
  selector: 'app-venue-form-basic',
  templateUrl: './venue-form-basic.component.html',
  styleUrls: ['./venue-form-basic.component.scss']
})
export class VenueFormBasicComponent implements OnInit, OnDestroy {
  @Input()
  new: boolean;
  @Input()
  venue: IVenue;
  venueForm: FormGroup;
  isLoading = false;
  error = '';
  sectors: ISector[];
  sectorNew: ISector;
  @Output() mapView = new EventEmitter<{longitude: number, latitude: number}>();

  constructor(private venueService: VenueService, private geocodeService: GeocodeService) { }

  ngOnInit() {
    this.venueForm = new FormGroup({
      name: new FormControl(null, Validators.required),
      city: new FormControl(null, Validators.required),
      street: new FormControl(null, Validators.required),
    });
    if (!this.new) {
      this.presetForm(this.venue);
      this.viewOnMap();
    }
    console.log(this.venue);
  }

  private presetForm(resData: IVenue) {
      this.venueForm = new FormGroup({
        name: new FormControl(resData.name, Validators.required),
        city: new FormControl(resData.city, Validators.required),
        street: new FormControl(resData.street, Validators.required)
      });
    }

    viewOnMap(){
      const { city, street } = this.venueForm.value;
      this.geocodeService.gelocate(city + ',' + street).subscribe(retData => {
        console.log(retData);
        this.mapView.emit({longitude: retData.results[0].geometry.location.lng, latitude: retData.results[0].geometry.location.lat});
      },error=>{
        console.log(error);
      });
    }

    get name(){
    return this.venueForm.get('name');
    }


  get city(){
    return this.venueForm.get('city');
  }

  get street(){
    return this.venueForm.get('street');
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

    const sectors: ISector[] = [{
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
      const venue: IVenue = {
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
          if (error.status === 401 || error.status === 400) {
            this.error = error.error.message;
          } else {
            this.error = 'Error';
          }
        }
      );

    } else {
      const venue: IVenueBasic = {
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
          if (error.status === 401 || error.status === 400) {
            this.error = error.error.message;
          } else {
            this.error = 'Error';
          }
        }
      );
    }

  });

  }

  ngOnDestroy(): void {
    this.mapView.unsubscribe();
  }

}
