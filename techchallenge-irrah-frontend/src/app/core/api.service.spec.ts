import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads conversations using the authenticated API contract', () => {
    service.conversations().subscribe();

    const request = http.expectOne('/api/conversations?size=100');
    expect(request.request.method).toBe('GET');
    request.flush({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 100 });
  });

  it('sends message content and priority to the selected conversation', () => {
    service.sendMessage('conversation-id', 'Mensagem urgente', 'URGENT').subscribe();

    const request = http.expectOne('/api/conversations/conversation-id/messages');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ content: 'Mensagem urgente', priority: 'URGENT' });
    request.flush({});
  });
});
