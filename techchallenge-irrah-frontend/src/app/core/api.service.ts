import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientProfile, Conversation, Message, MessagePriority, Page, Recipient } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  profile(): Observable<ClientProfile> {
    return this.http.get<ClientProfile>('/api/clients/me');
  }

  conversations(): Observable<Page<Conversation>> {
    return this.http.get<Page<Conversation>>('/api/conversations?size=100');
  }

  recipients(): Observable<Page<Recipient>> {
    return this.http.get<Page<Recipient>>('/api/recipients?size=100');
  }

  createConversation(recipientId: string): Observable<Conversation> {
    return this.http.post<Conversation>('/api/conversations', { recipientId });
  }

  messages(conversationId: string): Observable<Page<Message>> {
    return this.http.get<Page<Message>>(`/api/conversations/${conversationId}/messages?size=100`);
  }

  sendMessage(conversationId: string, content: string, priority: MessagePriority): Observable<Message> {
    return this.http.post<Message>(`/api/conversations/${conversationId}/messages`, { content, priority });
  }

  markAsRead(conversationId: string, messageId: string): Observable<Message> {
    return this.http.patch<Message>(
      `/api/conversations/${conversationId}/messages/${messageId}/read`,
      {}
    );
  }
}
