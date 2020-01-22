import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {ITicket} from '../../services/ticket.service';

@Component({
  selector: 'app-ticket-item',
  templateUrl: './ticket-item.component.html',
  styleUrls: ['./ticket-item.component.scss']
})
export class TicketItemComponent implements OnInit, AfterViewInit {

  @Input() ticket: ITicket;
  constructor() { }

  ngOnInit() {
    console.log(this.ticket);
  }

  ngAfterViewInit(): void {
    console.log('after view');
  }

}
