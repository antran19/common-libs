package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// Published by fulfillment-service once every line item of an order's
// InventoryReservationRequestedEvent was successfully reserved.
public class InventoryReservedEvent extends DomainEvent {

    private final String orderId;

    public InventoryReservedEvent(String orderId) {
        super("InventoryReserved", orderId);
        this.orderId = orderId;
    }

    @JsonCreator
    public InventoryReservedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
    }

    public String getOrderId() { return orderId; }
}
