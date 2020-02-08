import {Component, Input, OnInit} from '@angular/core';
import {ITicket} from '../../services/ticket.service';
import {EventService, IMediaFile} from '../../services/event.service';

@Component({
  selector: 'app-ticket-item',
  templateUrl: './ticket-item.component.html',
  styleUrls: ['./ticket-item.component.scss']
})
export class TicketItemComponent implements OnInit {
  @Input() ticket: ITicket;
  imageToShow: any;
  imageLoading = true;

  constructor(private eventService: EventService) {
  }

  ngOnInit() {
    this.eventService.getEventMediaFiles(this.ticket.eventId).subscribe((response: IMediaFile[]) => {
      if (response.length !== 0) {
        this.eventService.getEventImage(this.ticket.eventId, response[0].fileName).subscribe((blob: Blob) => {
          this.eventService.createImageFromBlob(blob, (readerResult) => {
            this.imageToShow = readerResult;
            this.imageLoading = false;
          });
        });
      }
    });
  }

}
