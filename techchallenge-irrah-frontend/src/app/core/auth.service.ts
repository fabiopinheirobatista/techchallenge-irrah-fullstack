import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from './models';

const SESSION_KEY = 'bcb.session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly sessionState = signal<AuthResponse | null>(this.restoreSession());

  readonly session = this.sessionState.asReadonly();
  readonly isAuthenticated = computed(() => {
    const session = this.sessionState();
    return !!session && new Date(session.expiresAt).getTime() > Date.now();
  });

  constructor(private readonly http: HttpClient) {}

  login(documentId: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth', { documentId, password }).pipe(
      tap((session) => this.saveSession(session))
    );
  }

  logout(): void {
    const token = this.sessionState()?.token;
    if (token) {
      this.http.post<void>('/api/auth/logout', {}).subscribe({ error: () => undefined });
    }
    this.sessionState.set(null);
    localStorage.removeItem(SESSION_KEY);
  }

  token(): string | null {
    return this.isAuthenticated() ? this.sessionState()?.token ?? null : null;
  }

  private saveSession(session: AuthResponse): void {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.sessionState.set(session);
  }

  private restoreSession(): AuthResponse | null {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      const session = raw ? (JSON.parse(raw) as AuthResponse) : null;
      if (!session || new Date(session.expiresAt).getTime() <= Date.now()) {
        localStorage.removeItem(SESSION_KEY);
        return null;
      }
      return session;
    } catch {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
  }
}
