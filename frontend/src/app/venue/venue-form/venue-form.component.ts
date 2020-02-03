import {Component, OnInit} from '@angular/core';
import {IVenue, IVenueBasic, VenueService} from '../../services/venue.service';
import {ActivatedRoute} from '@angular/router';
import {FormControl, FormGroup, FormGroupDirective, Validators} from '@angular/forms';
import {ISector} from '../../services/sector.service';
import {GeocodeService} from '../../services/geocode.service';
import {GridsterConfig, GridsterItem} from 'angular-gridster2';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-venue-form',
  templateUrl: './venue-form.component.html',
  styleUrls: ['./venue-form.component.scss']
})
export class VenueFormComponent implements OnInit {
  venue: IVenue;
  new: boolean;
  found: boolean;
  longitude: number;
  latitude: number;
  venueForm: FormGroup;
  isLoading = false;
  error = '';
  sectors: ISector[];

  items: GridsterItem[] = [];
  options: GridsterConfig;
  addSectorForm: FormGroup;

  constructor(private venueService: VenueService,
              private route: ActivatedRoute,
              private geocodeService: GeocodeService) {
  }

  ngOnInit() {
    const id = this.route.snapshot.params.id;
    this.found = false;
    if (id != null) {
      this.venueService.find(id).subscribe(resData => {
        this.venue = resData;
        this.found = true;
        this.presetForm(this.venue);
        this.viewOnMap();
      });
      this.new = false;
    } else {
      this.new = true;
      this.found = true;
      this.venueForm = new FormGroup({
        name: new FormControl(null, Validators.required),
        city: new FormControl(null, Validators.required),
        street: new FormControl(null, Validators.required),
      });
    }

    this.addSectorForm = new FormGroup({
      sectorName: new FormControl({value: '', disabled: !this.new}, [Validators.required]),
      maxCapacity: new FormControl({value: '', disabled: !this.new}, [Validators.required, Validators.min(1)])
    });

    this.options = {
      pushItems: true,
      gridType: 'fit',
      resizable: {
        enabled: true
      },
      draggable: {
        enabled: true
      },
      setGridSize: true,
      minCols: 50,
      maxCols: 500,
      minRows: 50,
      maxRows: 500
    };
  }

  private presetForm(resData: IVenue) {
    this.venueForm = new FormGroup({
      name: new FormControl(resData.name, Validators.required),
      city: new FormControl(resData.city, Validators.required),
      street: new FormControl(resData.street, Validators.required)
    });
  }

  viewOnMap() {
    const {city, street} = this.venueForm.value;
    this.geocodeService.gelocate(city + ',' + street).subscribe(retData => {
      this.longitude = retData.results[0].geometry.location.lng;
      this.latitude = retData.results[0].geometry.location.lat;
    }, error => console.log(error));
  }

  onSubmit() {
    if (!this.venueForm.valid) {
      this.error = 'Wrong inputs!';
      return;
    }

    this.isLoading = true;

    const {name, city, street} = this.venueForm.value;

    let latitude = 0.0;
    let longitude = 0.0;

    this.geocodeService.gelocate(city + ',' + street).subscribe(retData => {
      latitude = retData.results[0].geometry.location.lat;
      longitude = retData.results[0].geometry.location.lng;

      const isActive = true;
      const id = null;

      const sectors = this.items.map(item => (
        {
          startRow: item.y,
          startColumn: item.x,
          columnsCount: item.cols,
          rowsCount: item.rows,
          name: item.name,
          maxCapacity: item.capacity
        }));

      this.error = '';
      if (this.new) {
        if (sectors.length === 0) {
          Swal.fire({ icon: 'warning', text: 'Venue must contain at least 1 sector', toast: true, position: 'top-left'});
          return;
        }
        const venue: IVenue = {id, name, city, street, latitude, longitude, isActive, sectors};

        this.venueService.create(venue).subscribe(
          resData => {
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
        const venue: IVenueBasic = {name, city, street, latitude, longitude};
        this.venueService.update(this.venue.id, venue).subscribe(
          resData => {
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
      }
    });
  }

  onAddSector(formDirective: FormGroupDirective) {
    if (this.addSectorForm.invalid) {
      return;
    }
    this.items.push({
      cols: 10, rows: 10, y: 0, x: 0, minItemCols: 10, maxItemCols: 200, minItemRows: 1, maxItemRows: 200, maxItemArea: 40000,
      name: this.addSectorForm.get('sectorName').value, capacity: this.addSectorForm.get('maxCapacity').value
    });

    formDirective.resetForm();
    this.addSectorForm.reset();
  }

  onRemoveItem(item: any) {
    setTimeout(() => {
      this.items.splice(this.items.indexOf(item), 1);
    }, 0);
  }

  get name() {
    return this.venueForm.get('name');
  }

  get city() {
    return this.venueForm.get('city');
  }

  get street() {
    return this.venueForm.get('street');
  }

  get sectorName() {
    return this.addSectorForm.get('sectorName');
  }

  get maxCapacity() {
    return this.addSectorForm.get('maxCapacity');
  }
}
