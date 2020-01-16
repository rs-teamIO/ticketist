export class User {

  constructor(
    private _token: string,
    private _tokenExpirationDate: Date,
    public authority: string
  ) {}

  get token() {
    if (!this._tokenExpirationDate || new Date() > this._tokenExpirationDate) {
      return null;
    }
    return this._token;
  }
}
