package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// Published by fulfillment-service whenever a product's sellable stock changes (intake, reserve,
// release, commit), so other services can keep their own copy without calling it. Keyed by the
// PRODUCT id, the same id fulfillment stores as sku_id (see ReservationLineItem). Consumers should
// ignore an event older than the one they already applied (compare occurredAt): Kafka may
// redeliver.
public class InventoryLevelChangedEvent extends DomainEvent {

    private final String productId;
    private final int availableQuantity;

    public InventoryLevelChangedEvent(String productId, int availableQuantity) {
        super("InventoryLevelChanged", productId);
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }

    @JsonCreator
    public InventoryLevelChangedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("productId") String productId,
            @JsonProperty("availableQuantity") int availableQuantity) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() { return productId; }
    public int getAvailableQuantity() { return availableQuantity; }
}
