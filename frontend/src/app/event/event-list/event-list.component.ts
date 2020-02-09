import {
  AfterContentInit,
  AfterViewInit,
  Component,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {PageEvent} from '@angular/material';
import {IEventModel} from '../../model/event.model';
import {EventService, IEventPage} from '../../services/event.service';
import {Subscription} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('paginator', {static: false}) paginator: MatPaginator;
  events: IEventModel[] = [];
  totalSize: number;
  subscription: Subscription;

  constructor(private eventService: EventService) {
  }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.subscription = this.eventService.eventsChanged
      .subscribe((eventPage: IEventPage) => {
        this.events = eventPage.events;
        this.totalSize = eventPage.totalSize;
      });
    this.loadData();
  }

  onPageChanged(pageEvent: PageEvent) {
    this.eventService.pageChanged.next(pageEvent);
    this.loadData();
  }

  loadData() {
    this.eventService.getEvents();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
