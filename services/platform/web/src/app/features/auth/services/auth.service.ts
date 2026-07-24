import { Injectable } from "@angular/core";
import { LoginRequest } from "../dto/login-request";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { LoginResponse } from "../dto/login-response";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private http: HttpClient) {

    }

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
            '/api/v1/auth/login',
            request
        )
    }


}
