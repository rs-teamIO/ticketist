import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ITicket, TicketService} from '../services/ticket.service';
import {EventService} from '../services/event.service';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import Swal from 'sweetalert2'

declare var paypal;

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {

  @ViewChild('paypal', {static: true}) paypalElement: ElementRef;
  tickets: ITicket[] = [];
  ticketSubscription: Subscription;

  product = {
    price: 777.77,
    description: 'used couch, decent condition',
    img: 'assets/couch.jpg'
  };

  paidFor = false;

  constructor(private ticketService: TicketService,
              private eventService: EventService,
              private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.ticketSubscription = this.ticketService.ticketsSelected.subscribe(responseTickets => {
      this.tickets = responseTickets;
      // treba provera da li je prazno

    });



    this.route.paramMap.subscribe(params => {
      if (params.has('resId')) {
        this.ticketService.getReservationTickets(Number(params.get('resId'))).subscribe((response) => {
          this.tickets = response;
        }, (error) => {
          Swal.fire({
            title: 'Oops!',
            text: error.error.message,
            icon: 'error',
            // confirmButtonText: 'Cool'
          }).then(() => console.log('Vrati ga na stranicu neku, pocetnu recimo'));
        });
      }
    });

    paypal
      .Buttons({
        style: {
          size: 'responsive',
          layout: 'vertical',
          color: 'gold',
          shape: 'pill',
          label: 'checkout'
        },
        createOrder: (data, actions) => {
          console.log('Kreiranje ordera');
          return actions.order.create({
            purchase_units: [
              {
                description: this.product.description,
                amount: {
                  currency_code: 'USD',
                  value: this.product.price
                }
              }
            ]
          });
        },
        onApprove: async (data, actions) => {
          const order = await actions.order.capture();
          this.paidFor = true;
          console.log(order);
          console.log('Potvrdjeno');
        },
        onError: err => {
          console.log(err);
        }
      })
      .render(this.paypalElement.nativeElement);
  }

  private getTotalPrice(): number {
    let total = 0;
    this.tickets.forEach(ticket => total += ticket.price);
    return total;
  }


}
