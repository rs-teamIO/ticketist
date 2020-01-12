export class EventModel {

  constructor(private _id: number,
              public name: string,
              public venueName: string,
              public startDate: Date,
              public endDate: Date,
              public description: string) {
  }

  get id(): number {
    return this._id;
  }
}
