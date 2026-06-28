package com.br.desafioirrahbackend.domain;

import java.math.BigDecimal;

public enum MessagePriority {
    NORMAL("0.25"),
    URGENT("0.50");

    private final BigDecimal cost;

    MessagePriority(String cost) {
        this.cost = new BigDecimal(cost);
    }

    public BigDecimal cost() {
        return cost;
    }
}
