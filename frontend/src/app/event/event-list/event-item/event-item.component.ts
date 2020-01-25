import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EventModel} from '../../../model/event.model';
import Swal from 'sweetalert2';
import {EventService} from '../../../services/event.service';

@Component({
  selector: 'app-event-item',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.scss']
})
export class EventItemComponent implements OnInit {
  @Input() eventModel: EventModel;
  @Output() eventCancelled: EventEmitter<any> = new EventEmitter();

  constructor(private eventService: EventService) {
  }

  ngOnInit() {
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

}
