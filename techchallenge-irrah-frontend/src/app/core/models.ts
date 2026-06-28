export type Role = 'CLIENT' | 'ADMIN';
export type PlanType = 'PREPAID' | 'POSTPAID';
export type MessagePriority = 'NORMAL' | 'URGENT';
export type MessageStatus = 'QUEUED' | 'PROCESSING' | 'SENT' | 'FAILED' | 'RECEIVED';
export type MessageDirection = 'INBOUND' | 'OUTBOUND';

export interface AuthResponse {
  token: string;
  expiresAt: string;
  clientId: string;
  clientName: string;
  role: Role;
}

export interface ClientProfile {
  id: string;
  documentId: string;
  documentType: 'CPF' | 'CNPJ';
  name: string;
  planType: PlanType;
  balance: number | null;
  monthlyLimit: number | null;
  monthlyUsage: number | null;
  role: Role;
  active: boolean;
}

export interface Conversation {
  id: string;
  recipientId: string;
  recipientName: string;
  recipientContact: string;
  contactType: 'PHONE' | 'EMAIL';
  lastMessageContent: string | null;
  lastMessageTime: string | null;
  unreadCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Message {
  id: string;
  conversationId: string;
  content: string;
  direction: MessageDirection;
  priority: MessagePriority;
  status: MessageStatus;
  cost: number;
  failureReason: string | null;
  createdAt: string;
  sentAt: string | null;
  readAt: string | null;
}

export interface Recipient {
  id: string;
  name: string;
  contact: string;
  contactType: 'PHONE' | 'EMAIL';
  active: boolean;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ApiError {
  message?: string;
}
