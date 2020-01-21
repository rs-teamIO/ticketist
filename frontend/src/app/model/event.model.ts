export class EventModel {

  constructor(private _id?: number,
              public name?: string,
              public venueName?: string,
              public venueId?: number,
              public startDate?: Date,
              public endDate?: Date,
              public description?: string,
              public eventSectors?: {id: number, date: Date, sectorId: number}[]) {
  }

  get id(): number {
    return this._id;
  }
}