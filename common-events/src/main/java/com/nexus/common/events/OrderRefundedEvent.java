package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderRefundedEvent extends DomainEvent {

    private final String orderId;
    private final String buyerId;
    private final BigDecimal amount;
    private final String reason;

    public OrderRefundedEvent(String orderId, String buyerId, BigDecimal amount, String reason) {
        super("OrderRefunded", orderId);
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.reason = reason;
    }

    @JsonCreator
    public OrderRefundedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("buyerId") String buyerId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("reason") String reason) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.reason = reason;
    }

    public String getOrderId() { return orderId; }
    public String getBuyerId() { return buyerId; }
    public BigDecimal getAmount() { return amount; }
    public String getReason() { return reason; }
}
