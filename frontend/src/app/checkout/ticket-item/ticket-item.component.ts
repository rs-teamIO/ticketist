import {AfterViewInit, Component, Input, OnChanges, OnInit} from '@angular/core';
import {ITicket} from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-item',
  templateUrl: './ticket-item.component.html',
  styleUrls: ['./ticket-item.component.scss']
})
export class TicketItemComponent implements OnInit {
  @Input() ticket: ITicket;

  constructor() {
  }

  ngOnInit() {
  }
}
