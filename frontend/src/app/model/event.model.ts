import {IEventSector, IMediaFile} from '../services/event.service';

export class EventModel {

  constructor(private _id?: number,
              public name?: string,
              public venueName?: string,
              public venueId?: number,
              public startDate?: Date,
              public endDate?: Date,
              public description?: string,
              public reservationLimit?: number,
              public eventSectors?: IEventSector[],
              public mediaFiles?: IMediaFile[]) {
  }

  get id(): number {
    return this._id;
  }
}
