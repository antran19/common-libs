package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class OrderCancelledEvent extends DomainEvent {

    private final String orderId;
    private final String reason;

    public OrderCancelledEvent(String orderId, String reason) {
        super("OrderCancelled", orderId);
        this.orderId = orderId;
        this.reason = reason;
    }

    @JsonCreator
    public OrderCancelledEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("reason") String reason) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId() { return orderId; }
    public String getReason() { return reason; }
}
