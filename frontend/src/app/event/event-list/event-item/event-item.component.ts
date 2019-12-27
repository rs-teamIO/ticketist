import {Component, Input, OnInit} from '@angular/core';
import {Dogadjaj} from '../../../model/event.model';

@Component({
  selector: 'app-event-item',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.scss']
})
export class EventItemComponent implements OnInit {
  @Input() dogadjaj: Dogadjaj;

  constructor() {
  }

  ngOnInit() {
  }

}
