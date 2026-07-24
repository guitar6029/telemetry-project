import { Injectable } from "@angular/core";
import { LoginRequest } from "../dto/login-request";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { LoginResponse } from "../dto/login-response";
import { RegisterRequest } from "../dto/register-request";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private readonly AUTH_API = "/api/v1/auth";

    constructor(private http: HttpClient) {

    }

    login(request: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(
            `${this.AUTH_API}/login`,
            request
        )
    }

    register(request: RegisterRequest): Observable<void> {
        return this.http.post<void>(
            `${this.AUTH_API}/register`,
            request
        )
    }


}
