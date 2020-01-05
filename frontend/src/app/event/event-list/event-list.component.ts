import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, PageEvent} from '@angular/material';
import {EventModel} from '../../model/event.model';
import {EventService, IEventPage} from '../../services/event.service';
import {Page} from '../../model/page.model';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-event-list',
  templateUrl: './event-list.component.html',
  styleUrls: ['./event-list.component.scss']
})
export class EventListComponent implements OnInit, AfterViewInit, OnDestroy {
  // @ViewChild(MatPaginator, {static: false}) paginator: MatPaginator;
  events: EventModel[] = [];
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
    // this.eventService.getAll(new Page(this.paginator.pageIndex, this.paginator.pageSize));
    this.eventService.getEvents();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
