export class User {
  constructor(
    // tslint:disable-next-line:variable-name
    private _token: string,
    // tslint:disable-next-line:variable-name
    private _tokenExpirationDate: Date
  ) {}

  get token() {
    if (!this._tokenExpirationDate || new Date() > this._tokenExpirationDate) {
      return null;
    }
    return this._token;
  }
}
