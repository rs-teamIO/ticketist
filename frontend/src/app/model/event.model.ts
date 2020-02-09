import {IEventSector, IMediaFile} from '../services/event.service';

export interface IEventModel {
  id: number;
  name: string;
  venueName?: string;
  venueId: number;
  startDate?: Date;
  endDate?: Date;
  description?: string;
  reservationLimit?: number;
  eventSectors?: IEventSector[];
  mediaFiles?: IMediaFile[];
}
