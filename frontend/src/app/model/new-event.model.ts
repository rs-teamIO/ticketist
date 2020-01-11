import { IEvent, IEventSector, IEventBasic } from '../services/event.service';

export class NewEvent implements IEvent {

  constructor(
    public basicInfo: IEventBasic,
    public eventSectors: IEventSector[],
    public mediaFiles: any[]
  ) {}
}
