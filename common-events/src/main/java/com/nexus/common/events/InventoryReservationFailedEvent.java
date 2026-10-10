package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// Published by fulfillment-service when at least one line item of an order couldn't be
// reserved (out of stock). commerce-service reacts by cancelling the order -- the buyer sees
// "order cancelled, out of stock" shortly after checkout rather than a confirmed order they
// can never pay for. Since ReserveForOrderUseCase runs the whole order in one transaction,
// no partial holds are ever left behind for commerce-service to clean up.
public class InventoryReservationFailedEvent extends DomainEvent {

    private final String orderId;
    private final String reason;

    public InventoryReservationFailedEvent(String orderId, String reason) {
        super("InventoryReservationFailed", orderId);
        this.orderId = orderId;
        this.reason = reason;
    }

    @JsonCreator
    public InventoryReservationFailedEvent(
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
