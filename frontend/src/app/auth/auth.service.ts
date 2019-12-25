import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface RegisterUser {
    username: string;
    password: string;
    email: string;
    firstName: string;
    lastName: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    constructor(private http: HttpClient) { }

    signup(registerUser: RegisterUser) {
        // Change response type from any
        return this.http.post<any>(
            'http://localhost:8000/api/users',
            {
                username: registerUser.username,
                email: registerUser.email,
                password: registerUser.password,
                firstName: registerUser.firstName,
                lastName: registerUser.lastName
            }
        );
    }

    login(username: string, password: string) {
        // Change response type from any
        return this.http.post<any>(
            'http://localhost:8000/api/auth',
            {
                username,
                password
            }
        );
    }
}
