import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ITicket, TicketService} from '../services/ticket.service';
import {EventService} from '../services/event.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import Swal from 'sweetalert2';

declare var paypal;

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit, OnDestroy {
  @ViewChild('paypal', {static: true}) paypalElement: ElementRef;
  tickets: ITicket[] = [];
  ticketSubscription: Subscription;

  constructor(private ticketService: TicketService,
              private eventService: EventService,
              private router: Router,
              private route: ActivatedRoute) {

    // ToDo: Problem kod refresha, gubi se sve iz korpe (ako proba da ode obavestis ga da ce izgubiti sve?)
    this.ticketSubscription = this.ticketService.ticketsSelected.subscribe(responseTickets => {
      this.tickets = responseTickets;
    });
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      if (params.has('resId')) {
        this.ticketService.getReservationTickets(Number(params.get('resId'))).subscribe((response) => {
          this.tickets = response;
        }, (error) => {
          Swal.fire({
            title: 'Oops!',
            text: error.error.message,
            icon: 'error',
            allowOutsideClick: false,
            confirmButtonText: 'Cool, I\'ll go back to start page now',
          }).then(() => this.router.navigate(['/']));
        });
      }
    });

    paypal
      .Buttons({
        env: 'sandbox',
        style: {
          size: 'responsive',
          layout: 'vertical',
          color: 'gold',
          shape: 'pill',
          label: 'checkout'
        },
        createOrder: (data, actions) => {
          return actions.order.create({
            purchase_units: [
              {
                description: 'Tickets for ' + this.tickets[0].eventName,
                amount: {
                  currency_code: 'USD',
                  value: this.getTotalPrice()
                }
              }
            ]
          });
        },
        onApprove: async (data, actions) => {
          Swal.fire({
            text: 'Your payment is being processed, please wait',
            allowOutsideClick: false,
            showConfirmButton: false,
          });
          Swal.showLoading();

          this.ticketService.buyTickets(this.tickets.map(ticket => ticket.id)).subscribe((response: ITicket[]) => {
            actions.order.capture().then(order => {
              console.log(order);
              Swal.fire({
                icon: 'success',
                title: 'Payment successful!',
                allowEnterKey: true,
                allowOutsideClick: false,
                confirmButtonText: 'Okay',
                confirmButtonColor: '#673AB7'
              }).then(() => this.router.navigate(['/']));
            });
          }, error => {
            Swal.fire({
              icon: 'error',
              text: error.error.message,
              confirmButtonColor: '#673AB7',
              confirmButtonText: 'Okay, I\'ll go back to the front page then',
              allowOutsideClick: false
            }).then(() => this.router.navigate(['/']));
          });
        },
        onError: err => {
          Swal.fire({
            icon: 'error',
            text: err
          }).then(() => this.router.navigate(['/']));
        }
      }).render(this.paypalElement.nativeElement);
  }

  ngOnDestroy(): void {
    this.ticketSubscription.unsubscribe();
  }

  private getTotalPrice(): number {
    let total = 0;
    this.tickets.forEach(ticket => total += ticket.price);
    return total;
  }
}
