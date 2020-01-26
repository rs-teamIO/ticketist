import {Component, Input, OnChanges, OnInit, Renderer2, SimpleChanges, Inject, forwardRef} from '@angular/core';
import {EventModel} from '../../../model/event.model';
import {Venue} from '../../../model/venue.model';
import * as moment from 'moment';
import {FormControl, FormGroup} from '@angular/forms';
import {ITicket, TicketService} from '../../../services/ticket.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-seats-display',
  templateUrl: './seats-display.component.html',
  styleUrls: ['./seats-display.component.scss']
})
export class SeatsDisplayComponent implements OnInit, OnChanges {
  value = '';
  selectedSeats: ITicket[] = [];

  @Input() event: EventModel = new EventModel();
  @Input() venue: Venue = new Venue();
  dates: Date[] = [];

  eventSectorForm: FormGroup;
  seats: any[] = [];
  seatRows: number[] = [];
  seatsMap: { [key: number]: ITicket[] } = {};


  constructor(private ticketService: TicketService,
              private renderer: Renderer2,
              private router: Router) {
  }

  ngOnInit() {
    this.eventSectorForm = new FormGroup({
      eventDate: new FormControl({value: '', disabled: false}),
      selectedSector: new FormControl()
    });
  }

  onBuy() {
    this.router.navigate(['/checkout']).then(() => this.ticketService.ticketsSelected.next(this.selectedSeats));
  }

  onReserve() {
    return;
  }

  onLoadSectorSeats() {
    this.seatsMap = {};
    this.seatRows = [];

    this.event.eventSectors.forEach((el) => {
      if (moment(moment(el.date)).format('YYYY-MM-DD HH:mm:ss') === this.eventSectorForm.get('eventDate').value &&
        el.sectorId === this.eventSectorForm.get('selectedSector').value.id) {

        this.ticketService.getEventSectorTickets(el.id).subscribe(responseData => {
          this.seatRows = Array(this.eventSectorForm.get('selectedSector').value.rowsCount).fill(1);
          for (const t of responseData) {
            if (this.seatsMap.hasOwnProperty(t.numberRow)) {
              this.seatsMap[t.numberRow].push(t);
            } else {
              this.seatsMap[t.numberRow] = [t];
            }
          }
        });
      }
    });
  }

  onSeatSelected(event: MouseEvent, seat: ITicket) {
    // @ts-ignore
    if (event.target.classList.contains('selected')) {
      this.renderer.removeClass(event.target, 'selected');
      this.selectedSeats.splice(this.selectedSeats.indexOf(seat), 1);
    } else {
      this.renderer.addClass(event.target, 'selected');
      this.selectedSeats.push(seat);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.dates = this.getDates(this.event.startDate, this.event.endDate);
  }

  getDates(startDate, stoppDate) {
    const dateArray = [];
    let currDate = moment(startDate);
    const stopDate = moment(stoppDate);

    while (currDate <= stopDate) {
      dateArray.push(moment(currDate).format('YYYY-MM-DD HH:mm:ss'));
      currDate = moment(currDate).add(1, 'days');
    }
    return dateArray;
  }

  private getTotalPrice(): number {
    let total = 0;
    this.selectedSeats.forEach(seat => total += seat.price);
    return total;
  }
}
