import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { AuthResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = '/api/v1/auth';

  constructor(private http: HttpClient, private router: Router) {}

  register(data: { name: string; email: string; password: string; deviceId: string }) {
    return this.http.post<AuthResponse>(`${this.base}/register`, data).pipe(
      tap(res => this.saveSession(res))
    );
  }

  login(data: { email: string; password: string; deviceId: string }) {
    return this.http.post<AuthResponse>(`${this.base}/login`, data).pipe(
      tap(res => this.saveSession(res))
    );
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser() {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  }

  private saveSession(res: AuthResponse) {
    localStorage.setItem('token', res.accessToken);
    localStorage.setItem('user', JSON.stringify(res.user));
  }
}
