import {Sector} from './sector.model';

export class Venue {

  constructor(private _id?: number,
              public name?: string,
              public street?: string,
              public city?: string,
              public latitude?: number,
              public longitude?: number,
              public sectors?: Sector[]
  ) {
  }

  get id(): number {
    return this._id;
  }
}