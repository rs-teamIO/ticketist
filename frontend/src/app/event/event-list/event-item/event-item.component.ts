import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {EventModel} from '../../../model/event.model';
import Swal from 'sweetalert2';
import {EventService} from '../../../services/event.service';
import {AuthService} from '../../../services/auth.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-event-item',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.scss']
})
export class EventItemComponent implements OnInit, OnDestroy {
  @Input() eventModel: EventModel;
  @Output() eventCancelled: EventEmitter<any> = new EventEmitter();
  private userSub: Subscription;
  role = null;
  imageToShow: any;
  imageLoading = true;

  constructor(private eventService: EventService,
              private authService: AuthService) {
  }

  ngOnInit() {
    this.userSub = this.authService.user.subscribe(user => {
      if (!!user) {
        this.role = user.authority;
      } else {
        this.role = null;
      }
    });

    if (this.eventModel.mediaFiles.length !== 0) {
      this.eventService.getEventImage(this.eventModel.id, this.eventModel.mediaFiles[0].fileName).subscribe((response: Blob) => {
        this.imageToShow = this.createImageFromBlob(response);
        this.imageLoading = false;
      });
    }
  }

  onCancelEvent() {
    Swal.fire({
      icon: 'question',
      title: 'Are you sure you want to cancel ' + this.eventModel.name + '?',
      text: 'You won\'t be able to undo this action',
      confirmButtonText: 'I\'m sure',
      confirmButtonColor: '#673AB7',
      showCancelButton: true,
      cancelButtonText: 'I don\'t want to cancel this event',
      cancelButtonColor: 'firebrick'
    }).then(res => {
      if (res.value) {
        this.eventService.cancelEvent(this.eventModel.id).subscribe(response => {
          Swal.fire({
            icon: 'success',
            text: response.name + ' cancelled',
            confirmButtonColor: '#673AB7'
          }).then(() => this.eventCancelled.emit(null));
        }, error => Swal.fire({icon: 'error', text: error.error.message}));

      }
    });
  }

  createImageFromBlob(image: Blob) {
    const reader = new FileReader();
    reader.addEventListener('load', () => {
      this.imageToShow =  reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

}
