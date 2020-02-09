import {Component, OnInit, Input} from '@angular/core';
import {IReservation, ReservationService} from 'src/app/services/reservation.service';
import {EventService, IMediaFile} from '../../services/event.service';

@Component({
  selector: 'app-reservation-item',
  templateUrl: './reservation-item.component.html',
  styleUrls: ['./reservation-item.component.scss']
})
export class ReservationItemComponent implements OnInit {
  @Input() reservation: IReservation;
  errorMessage = '';
  imageToShow: any;
  imageLoading = true;

  constructor(private reservationService: ReservationService, private eventService: EventService) {}

  ngOnInit() {
    this.eventService.getEventMediaFiles(this.reservation.eventId).subscribe((response: IMediaFile[]) =>{
      if (response.length !== 0 ) {
        this.eventService.getEventImage(this.reservation.eventId, response[0].fileName).subscribe((blob: Blob) => {
          this.eventService.createImageFromBlob(blob, (readerResponse) => {
            this.imageToShow = readerResponse;
            this.imageLoading = false;
          });
        });
      }
    });
  }

  cancelReservation() {
    this.reservationService.cancelReservation(this.reservation.id)
      .subscribe(resData => {
          console.log(resData);
          this.reservationService.getReservations();
        },
        error => {
          console.log('Error: ', error);
          if (error.status === 400) {
            this.errorMessage = error.error.message;
          }
        }
      );
  }

}
