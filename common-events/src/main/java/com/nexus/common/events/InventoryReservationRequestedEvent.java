package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

// Published by commerce-service right after an order is created (both direct-purchase
// checkout and auction settlement) so fulfillment-service can try to hold the stock. The
// order already exists by the time this fires -- see InventoryReservedEvent /
// InventoryReservationFailedEvent for how commerce-service learns the outcome.
public class InventoryReservationRequestedEvent extends DomainEvent {

    private final String orderId;
    private final List<ReservationLineItem> items;

    public InventoryReservationRequestedEvent(String orderId, List<ReservationLineItem> items) {
        super("InventoryReservationRequested", orderId);
        this.orderId = orderId;
        this.items = items;
    }

    @JsonCreator
    public InventoryReservationRequestedEvent(
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
