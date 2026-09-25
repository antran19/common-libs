package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ProductUpdatedEvent extends DomainEvent {

    private final String productId;
    private final String name;

    public ProductUpdatedEvent(String productId, String name) {
        super("ProductUpdated", productId);
        this.productId = productId;
        this.name = name;
    }

    @JsonCreator
    public ProductUpdatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("productId") String productId,
            @JsonProperty("name") String name) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.name = name;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
}
