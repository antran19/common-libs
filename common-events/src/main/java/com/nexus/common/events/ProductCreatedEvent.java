package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ProductCreatedEvent extends DomainEvent {

    private final String productId;
    private final String name;
    private final String categoryId;

    public ProductCreatedEvent(String productId, String name, String categoryId) {
        super("ProductCreated", productId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
    }

    @JsonCreator
    public ProductCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("productId") String productId,
            @JsonProperty("name") String name,
            @JsonProperty("categoryId") String categoryId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }
}
