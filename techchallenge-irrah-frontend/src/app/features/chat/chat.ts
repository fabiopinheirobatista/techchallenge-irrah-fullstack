import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, interval } from 'rxjs';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ApiError, ClientProfile, Conversation, Message, Recipient } from '../../core/models';

@Component({
  selector: 'app-chat',
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe],
  templateUrl: './chat.html',
  styleUrl: './chat.scss'
})
export class Chat {
  private readonly destroyRef = inject(DestroyRef);

  readonly profile = signal<ClientProfile | null>(null);
  readonly conversations = signal<Conversation[]>([]);
  readonly messages = signal<Message[]>([]);
  readonly recipients = signal<Recipient[]>([]);
  readonly selectedConversation = signal<Conversation | null>(null);
  readonly loadingConversations = signal(true);
  readonly loadingMessages = signal(false);
  readonly sending = signal(false);
  readonly error = signal('');
  readonly newConversationOpen = signal(false);
  readonly creatingConversation = signal(false);
  readonly search = signal('');

  readonly filteredConversations = computed(() => {
    const term = this.search().trim().toLocaleLowerCase('pt-BR');
    return term
      ? this.conversations().filter((item) =>
          `${item.recipientName} ${item.recipientContact}`.toLocaleLowerCase('pt-BR').includes(term))
      : this.conversations();
  });

  readonly availableAmount = computed(() => {
    const profile = this.profile();
    if (!profile) return null;
    return profile.planType === 'PREPAID'
      ? profile.balance
      : (profile.monthlyLimit ?? 0) - (profile.monthlyUsage ?? 0);
  });

  readonly messageForm;
  readonly conversationForm;

  constructor(
    private readonly api: ApiService,
    readonly auth: AuthService,
    private readonly router: Router,
    formBuilder: FormBuilder
  ) {
    this.messageForm = formBuilder.nonNullable.group({
      content: ['', [Validators.required, Validators.maxLength(2000)]],
      priority: ['NORMAL' as 'NORMAL' | 'URGENT', Validators.required]
    });
    this.conversationForm = formBuilder.nonNullable.group({
      recipientId: ['', Validators.required]
    });

    this.loadInitialData();
    interval(3000).pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      if (this.selectedConversation() && !this.sending()) this.loadMessages(true);
    });
  }

  selectConversation(conversation: Conversation): void {
    this.selectedConversation.set(conversation);
    this.loadMessages();
  }

  closeConversation(): void {
    this.selectedConversation.set(null);
    this.messages.set([]);
  }

  send(): void {
    const conversation = this.selectedConversation();
    if (!conversation || this.messageForm.invalid || this.sending()) return;
    this.sending.set(true);
    this.error.set('');
    const { content, priority } = this.messageForm.getRawValue();
    this.api.sendMessage(conversation.id, content, priority).pipe(
      finalize(() => this.sending.set(false))
    ).subscribe({
      next: (message) => {
        this.messages.update((items) => [...items, message]);
        this.messageForm.controls.content.reset();
        this.loadProfile();
        this.loadConversations();
      },
      error: (response) => this.error.set(this.apiError(response, 'Não foi possível enviar a mensagem.'))
    });
  }

  openNewConversation(): void {
    this.newConversationOpen.set(true);
    if (this.recipients().length) return;
    this.api.recipients().subscribe({
      next: (page) => this.recipients.set(page.content.filter((item) => item.active)),
      error: () => this.error.set('Não foi possível carregar os contatos.')
    });
  }

  createConversation(): void {
    if (this.conversationForm.invalid || this.creatingConversation()) return;
    this.creatingConversation.set(true);
    this.api.createConversation(this.conversationForm.getRawValue().recipientId).pipe(
      finalize(() => this.creatingConversation.set(false))
    ).subscribe({
      next: (conversation) => {
        this.newConversationOpen.set(false);
        this.conversationForm.reset();
        this.loadConversations(conversation.id);
      },
      error: () => this.error.set('Não foi possível iniciar a conversa.')
    });
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigate(['/login']);
  }

  handleEnter(event: Event): void {
    const keyboardEvent = event as KeyboardEvent;
    if (!keyboardEvent.shiftKey) {
      keyboardEvent.preventDefault();
      this.send();
    }
  }

  trackConversation(_: number, item: Conversation): string { return item.id; }
  trackMessage(_: number, item: Message): string { return item.id; }

  statusLabel(message: Message): string {
    const labels: Record<string, string> = {
      QUEUED: 'Na fila', PROCESSING: 'Enviando', SENT: 'Enviada', FAILED: 'Falhou', RECEIVED: 'Recebida'
    };
    return labels[message.status] ?? message.status;
  }

  private loadInitialData(): void {
    this.loadProfile();
    this.loadConversations();
  }

  private loadProfile(): void {
    this.api.profile().subscribe({
      next: (profile) => this.profile.set(profile),
      error: () => this.error.set('Não foi possível carregar os dados da conta.')
    });
  }

  private loadConversations(selectId?: string): void {
    this.loadingConversations.set(true);
    this.api.conversations().pipe(
      finalize(() => this.loadingConversations.set(false))
    ).subscribe({
      next: (page) => {
        this.conversations.set(page.content);
        const target = selectId ? page.content.find((item) => item.id === selectId) : undefined;
        if (target) this.selectConversation(target);
      },
      error: () => this.error.set('Não foi possível carregar as conversas.')
    });
  }

  private loadMessages(silent = false): void {
    const conversation = this.selectedConversation();
    if (!conversation) return;
    if (!silent) this.loadingMessages.set(true);
    this.api.messages(conversation.id).pipe(
      finalize(() => this.loadingMessages.set(false))
    ).subscribe({
      next: (page) => {
        if (this.selectedConversation()?.id !== conversation.id) return;
        const ordered = [...page.content].reverse();
        this.messages.set(ordered);
        ordered.filter((message) => message.direction === 'INBOUND' && !message.readAt)
          .forEach((message) => this.api.markAsRead(conversation.id, message.id).subscribe());
      },
      error: () => { if (!silent) this.error.set('Não foi possível carregar as mensagens.'); }
    });
  }

  private apiError(response: { error?: ApiError }, fallback: string): string {
    return response.error?.message || fallback;
  }
}
