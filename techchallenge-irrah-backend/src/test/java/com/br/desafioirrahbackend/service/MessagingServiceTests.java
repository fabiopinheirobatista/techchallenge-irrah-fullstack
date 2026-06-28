package com.br.desafioirrahbackend.service;

import com.br.desafioirrahbackend.domain.Client;
import com.br.desafioirrahbackend.domain.ContactType;
import com.br.desafioirrahbackend.domain.Conversation;
import com.br.desafioirrahbackend.domain.DocumentType;
import com.br.desafioirrahbackend.domain.FinancialTransaction;
import com.br.desafioirrahbackend.domain.Message;
import com.br.desafioirrahbackend.domain.MessagePriority;
import com.br.desafioirrahbackend.domain.MessageStatus;
import com.br.desafioirrahbackend.domain.PlanType;
import com.br.desafioirrahbackend.domain.Recipient;
import com.br.desafioirrahbackend.dto.MessageRequest;
import com.br.desafioirrahbackend.dto.MessageResponse;
import com.br.desafioirrahbackend.exception.ApiException;
import com.br.desafioirrahbackend.repository.ClientRepository;
import com.br.desafioirrahbackend.repository.FinancialTransactionRepository;
import com.br.desafioirrahbackend.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTests {

    @Mock
    private ConversationService conversationService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private FinancialTransactionRepository transactionRepository;

    private MessagingService messagingService;
    private Client client;
    private Conversation conversation;
    private UUID clientId;
    private UUID conversationId;

    @BeforeEach
    void setUp() {
        messagingService = new MessagingService(conversationService, clientRepository,
                messageRepository, transactionRepository);
        clientId = UUID.randomUUID();
        conversationId = UUID.randomUUID();
        client = new Client("11144477735", DocumentType.CPF, "Client", "hash", PlanType.PREPAID);
        client.addCredit(BigDecimal.ONE);
        ReflectionTestUtils.setField(client, "id", clientId);
        Recipient recipient = new Recipient("Recipient", "+5511999999999", ContactType.PHONE);
        conversation = new Conversation(client, recipient);
        ReflectionTestUtils.setField(conversation, "id", conversationId);
        when(conversationService.findOwned(conversationId, clientId)).thenReturn(conversation);
    }

    @Test
    void shouldChargeAndQueueOutboundMessageAtomically() {
        when(clientRepository.findByIdForUpdate(clientId)).thenReturn(Optional.of(client));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            ReflectionTestUtils.setField(message, "id", UUID.randomUUID());
            return message;
        });

        MessageResponse response = messagingService.send(clientId, conversationId,
                new MessageRequest("Hello", MessagePriority.NORMAL));

        assertThat(response.status()).isEqualTo(MessageStatus.QUEUED);
        assertThat(response.cost()).isEqualByComparingTo("0.25");
        assertThat(client.getBalance()).isEqualByComparingTo("0.75");
        ArgumentCaptor<FinancialTransaction> transaction = ArgumentCaptor.forClass(FinancialTransaction.class);
        verify(transactionRepository).save(transaction.capture());
        assertThat(transaction.getValue().getAmount()).isEqualByComparingTo("-0.25");
    }

    @Test
    void shouldRejectMessageWhenFundsAreInsufficient() {
        Client emptyClient = new Client("11144477735", DocumentType.CPF, "Client", "hash", PlanType.PREPAID);
        ReflectionTestUtils.setField(emptyClient, "id", clientId);
        when(clientRepository.findByIdForUpdate(clientId)).thenReturn(Optional.of(emptyClient));

        assertThatThrownBy(() -> messagingService.send(clientId, conversationId,
                new MessageRequest("Hello", MessagePriority.NORMAL)))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.getStatus()).isEqualTo(HttpStatus.PAYMENT_REQUIRED));
        verify(messageRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}
