import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { AuthResponse } from './models';

describe('AuthService', () => {
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('authenticates and persists a valid session', () => {
    const session: AuthResponse = {
      token: 'token-id',
      expiresAt: new Date(Date.now() + 60_000).toISOString(),
      clientId: 'client-id',
      clientName: 'Ana Silva',
      role: 'CLIENT'
    };

    service.login('52998224725', 'Client@123').subscribe();
    const request = http.expectOne('/api/auth');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ documentId: '52998224725', password: 'Client@123' });
    request.flush(session);

    expect(service.isAuthenticated()).toBe(true);
    expect(service.token()).toBe('token-id');
    expect(JSON.parse(localStorage.getItem('bcb.session') ?? '{}').clientName).toBe('Ana Silva');
  });

  it('starts unauthenticated without a stored session', () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.token()).toBeNull();
  });
});
