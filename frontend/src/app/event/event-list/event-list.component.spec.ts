import {EventListComponent} from './event-list.component';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {EventService} from '../../services/event.service';
import {EventItemComponent} from './event-item/event-item.component';
import {CustomMaterialModule} from "../../shared/material.module";
import {RouterTestingModule} from "@angular/router/testing";
import {PageEvent} from "@angular/material/paginator";

describe('EventListComponent', () => {
  let component: EventListComponent;
  let fixture: ComponentFixture<EventListComponent>;
  let eventService: any;

  const aa = {
    events: [{}, {}, {}],
    totalSize: 8
};

  const pageEvent = new PageEvent();


  beforeEach(() => {
    const eventServiceMock = {
      getEvents: jasmine.createSpy('getEvents'),
      pageChanged: {
        next: jasmine.createSpy('next')
      },
      eventsChanged: {
        subscribe: jasmine.createSpy('subscribe')
      }
    };

    TestBed.configureTestingModule({
      declarations: [EventListComponent, EventItemComponent],
      providers: [{provide: EventService, useValue: eventServiceMock}],
      imports: [CustomMaterialModule, RouterTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(EventListComponent);
    component = fixture.componentInstance;
    eventService = TestBed.get(EventService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to event changes after view init', () => {
    component.ngAfterViewInit();
    expect(eventService.eventsChanged.subscribe).toHaveBeenCalled();
    expect(eventService.getEvents).toHaveBeenCalled();
  });

  it('should get events on load data', () => {
    component.loadData();
    expect(eventService.getEvents).toHaveBeenCalled();
  });

  it('should emit page event on page changed', () => {
    component.onPageChanged(pageEvent);
    expect(eventService.pageChanged.next).toHaveBeenCalledWith(pageEvent);
  });

});
