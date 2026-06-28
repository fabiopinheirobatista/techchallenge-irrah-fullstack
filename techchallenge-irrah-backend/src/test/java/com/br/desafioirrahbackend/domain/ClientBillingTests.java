package com.br.desafioirrahbackend.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientBillingTests {

    @Test
    void shouldDebitPrepaidBalance() {
        Client client = client(PlanType.PREPAID);
        client.addCredit(new BigDecimal("1.00"));

        client.charge(MessagePriority.URGENT.cost());

        assertThat(client.getBalance()).isEqualByComparingTo("0.50");
    }

    @Test
    void shouldRejectPrepaidChargeWithoutBalance() {
        Client client = client(PlanType.PREPAID);

        assertThatThrownBy(() -> client.charge(MessagePriority.NORMAL.cost()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient balance");
    }

    @Test
    void shouldTrackPostpaidUsageAndEnforceLimit() {
        Client client = client(PlanType.POSTPAID);
        client.adjustMonthlyLimit(new BigDecimal("0.50"));

        client.charge(MessagePriority.NORMAL.cost());
        client.charge(MessagePriority.NORMAL.cost());

        assertThat(client.getMonthlyUsage()).isEqualByComparingTo("0.50");
        assertThatThrownBy(() -> client.charge(MessagePriority.NORMAL.cost()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Monthly limit exceeded");
    }

    private Client client(PlanType planType) {
        return new Client("11144477735", DocumentType.CPF, "Test client", "hash", planType);
    }
}
