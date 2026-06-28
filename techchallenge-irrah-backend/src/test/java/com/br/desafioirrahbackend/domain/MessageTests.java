package com.br.desafioirrahbackend.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTests {

    @Test
    void shouldApplyPriorityCostAndLifecycle() {
        Message message = new Message(conversation(), " urgent message ", MessagePriority.URGENT);

        assertThat(message.getContent()).isEqualTo("urgent message");
        assertThat(message.getCost()).isEqualByComparingTo("0.50");
        assertThat(message.getStatus()).isEqualTo(MessageStatus.QUEUED);

        message.process();
        message.sent();

        assertThat(message.getStatus()).isEqualTo(MessageStatus.SENT);
        assertThat(message.getProcessingAt()).isNotNull();
        assertThat(message.getSentAt()).isNotNull();
    }

    @Test
    void shouldOnlyMarkInboundMessagesAsRead() {
        Message outbound = new Message(conversation(), "outbound", MessagePriority.NORMAL);
        Message inbound = Message.inbound(conversation(), "inbound");

        inbound.read();

        assertThat(inbound.getReadAt()).isNotNull();
        assertThatThrownBy(outbound::read).isInstanceOf(IllegalStateException.class);
    }

    private Conversation conversation() {
        Client client = new Client("11144477735", DocumentType.CPF, "Client", "hash", PlanType.PREPAID);
        Recipient recipient = new Recipient("Recipient", "+5511999999999", ContactType.PHONE);
        return new Conversation(client, recipient);
    }
}
