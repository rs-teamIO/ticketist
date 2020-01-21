export class Sector {

    constructor(public id: number,
                public name: string,
                public rowsCount: number,
                public columnsCount: number,
                public maxCapacity: number,
                public startRow: number,
                public startColumn: number) {
    }
  }