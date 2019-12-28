import {Component, Input, OnInit} from '@angular/core';
import {EventModel} from '../../../model/event.model';

@Component({
  selector: 'app-event-item',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.scss']
})
export class EventItemComponent implements OnInit {
  @Input() eventModel: EventModel;

  constructor() {
  }

  ngOnInit() {
  }

}
