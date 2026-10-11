package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

// Published by commerce-service when an Admin/Support Staff marks a ReturnRequest as
// RECEIVED (the physical goods are confirmed back). fulfillment-service consumes this to
// restock the returned items -- see IntakeInventoryUseCase.
public class OrderReturnReceivedEvent extends DomainEvent {

    private final String orderId;
    private final List<ReservationLineItem> items;

    public OrderReturnReceivedEvent(String orderId, List<ReservationLineItem> items) {
        super("OrderReturnReceived", orderId);
        this.orderId = orderId;
        this.items = items;
    }

    @JsonCreator
    public OrderReturnReceivedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("items") List<ReservationLineItem> items) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.items = items;
    }

    public String getOrderId() { return orderId; }
    public List<ReservationLineItem> getItems() { return items; }
}
