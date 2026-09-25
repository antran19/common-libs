package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ProductStatusChangedEvent extends DomainEvent {

    private final String productId;
    private final String oldStatus;
    private final String newStatus;

    public ProductStatusChangedEvent(String productId, String oldStatus, String newStatus) {
        super("ProductStatusChanged", productId);
        this.productId = productId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @JsonCreator
    public ProductStatusChangedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("productId") String productId,
            @JsonProperty("oldStatus") String oldStatus,
            @JsonProperty("newStatus") String newStatus) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public String getProductId() { return productId; }
    public String getOldStatus() { return oldStatus; }
    public String getNewStatus() { return newStatus; }
}
