import {TicketItemComponent} from './ticket-item.component';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {EventService} from '../../services/event.service';
import {CustomMaterialModule} from '../../shared/material.module';
import {RouterTestingModule} from '@angular/router/testing';
import {of} from 'rxjs';

describe('TicketItemComponent', () => {
  let component: TicketItemComponent;
  let fixture: ComponentFixture<TicketItemComponent>;
  let eventService: any;

  beforeEach(() => {
    const eventServiceMock = {
      getEventMediaFiles: jasmine.createSpy('getEventMediaFiles')
        .and.returnValue(of([ {id: 1, fileName: 'filename', mimeType: 'image/jpeg'}])),
      getEventImage: jasmine.createSpy('getEventImage')
        .and.returnValue(of(new Blob(['i', 'm', 'g'], {type: 'image/jpeg'}))),
      createImageFromBlob: jasmine.createSpy('createImageFromBlob')
    };

    TestBed.configureTestingModule({
      declarations: [TicketItemComponent],
      providers: [{provide: EventService, useValue: eventServiceMock}],
      imports: [CustomMaterialModule, RouterTestingModule]
    }).compileComponents();

    fixture = TestBed.createComponent(TicketItemComponent);
    component = fixture.componentInstance;
    component.ticket = {
      id: 1,
      numberRow: 1,
      numberColumn: 1,
      price: 35,
      status: 'PAID',
      eventSectorId: 1,
      eventId: 1,
      userId: 1,
      eventName: 'Event name',
      venueName: 'Venue name',
      sectorName: 'Sector name',
      date: new Date()
    };

    eventService = TestBed.get(EventService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get event image if there are event images available', () => {
    component.ngOnInit();
    expect(eventService.getEventMediaFiles).toHaveBeenCalledWith(1);
    expect(eventService.getEventImage).toHaveBeenCalledWith(1, 'filename');
    expect(eventService.createImageFromBlob).toHaveBeenCalled();
  });
});
