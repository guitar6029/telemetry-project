import { inject, Injectable } from "@angular/core";
import { LoginRequest } from "../dto/login-request.dto";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ApiConstants } from "../../../constants/api.constants";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private readonly authUrl = `${ApiConstants.API_V1}/auth`;
    private readonly http = inject(HttpClient)

    login(request: LoginRequest): Observable<void> {
        return this.http.post<void>(
            `${this.authUrl}/login`,
            request,
            {
                withCredentials: true
            }
        )
    }

    logout(): Observable<void> {
        return this.http.post<void>(
            `${this.authUrl}/logout`,
            {},
            {
                withCredentials: true
            }
        )
    }


}
