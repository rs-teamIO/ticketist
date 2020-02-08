import {Component, Input, OnChanges, OnInit, Renderer2, SimpleChanges, Inject, forwardRef} from '@angular/core';
import {EventModel} from '../../../model/event.model';
import {Venue} from '../../../model/venue.model';
import * as moment from 'moment';
import {FormControl, FormGroup} from '@angular/forms';
import {ITicket, TicketService} from '../../../services/ticket.service';
import {Router} from '@angular/router';
import Swal from 'sweetalert2';
import {IEventSector} from '../../../services/event.service';

@Component({
  selector: 'app-seats-display',
  templateUrl: './seats-display.component.html',
  styleUrls: ['./seats-display.component.scss']
})
export class SeatsDisplayComponent implements OnInit, OnChanges {
  selectedSeats: ITicket[] = [];
  selectedEventSector: IEventSector = {} as IEventSector;
  notNumeratedTickets: ITicket[] = [];

  @Input() event: EventModel = new EventModel();
  @Input() venue: Venue = new Venue();
  dates: Date[] = [];

  eventSectorForm: FormGroup;

  seatRows: number[] = [];
  seatsMap: { [key: number]: ITicket[] } = {};
  counterValue = 0;

  sectorInactive = false;

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
    if (this.selectedSeats.length <= this.event.reservationLimit) {
      this.ticketService.reserveTickets(this.selectedSeats.map(seat => seat.id)).subscribe(
        (response) => {
          Swal.fire({
            icon: 'success',
            title: 'Reservation successful',
            text: 'You will be redirected to front page now',
            timer: 3000
          })
            .then(() => this.router.navigate(['/']));
        }, (error) => {
          Swal.fire({icon: 'error', title: 'Reservation failed', text: error.error.message});
        });
    } else {
      Swal.fire({icon: 'error', text: 'You can reserve ' + this.event.reservationLimit + ' seats maximum'});
    }
  }

  onLoadSectorSeats() {
    this.seatsMap = {};
    this.seatRows = [];
    this.sectorInactive = false;

    this.event.eventSectors.forEach((el) => {
      if (moment(moment(el.date)).format('YYYY-MM-DD HH:mm:ss') === this.eventSectorForm.get('eventDate').value &&
        el.sectorId === this.eventSectorForm.get('selectedSector').value.id) {

        this.selectedEventSector = el;

        this.ticketService.getEventSectorTickets(el.id).subscribe(responseData => {

          if (el.numeratedSeats) {
            this.seatRows = Array(this.eventSectorForm.get('selectedSector').value.rowsCount).fill(1);
            for (const t of responseData) {
              if (this.seatsMap.hasOwnProperty(t.numberRow)) {
                this.seatsMap[t.numberRow].push(t);
              } else {
                this.seatsMap[t.numberRow] = [t];
              }
            }
          } else {
            this.notNumeratedTickets = responseData.filter((ticket: ITicket) => ticket.status === 'FREE');
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

  onAddTicket() {
    const ticket = this.notNumeratedTickets.length !== 0 ? this.notNumeratedTickets[0] : null;
    if (ticket) {
      this.selectedSeats.push(ticket);
      this.notNumeratedTickets.splice(this.notNumeratedTickets.indexOf(ticket), 1);
      this.counterValue++;
    } else {
      Swal.fire({icon: 'warning', text: 'No more tickets available for this sector', toast: true});
    }
  }

  onRemoveTicket() {
    if (this.counterValue > 0) {
      const removedSeat = this.selectedSeats.find(t => t.numberRow === -1 && t.numberColumn === -1);
      this.selectedSeats.splice(this.selectedSeats.indexOf(removedSeat), 1);
      this.notNumeratedTickets.push(removedSeat);
      this.counterValue = this.counterValue -= 1;
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

  isSectorActive(sectorId: number): boolean {
    if (this.event.eventSectors.find(vel => vel.sectorId === sectorId)) {
      return true;
    }
    return false;
  }
}
