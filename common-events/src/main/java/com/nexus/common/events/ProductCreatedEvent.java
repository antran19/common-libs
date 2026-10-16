package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// sellerId and initialStock let fulfillment-service register who owns the product's stock and
// create the initial inventory record without calling catalog-service. Both are optional on
// the wire: events published before they existed deserialize with sellerId = null and
// initialStock = 0, and the 3-argument constructor keeps old callers compiling.
public class ProductCreatedEvent extends DomainEvent {

    private final String productId;
    private final String name;
    private final String categoryId;
    private final String sellerId;
    private final int initialStock;

    public ProductCreatedEvent(String productId, String name, String categoryId) {
        this(productId, name, categoryId, null, 0);
    }

    public ProductCreatedEvent(String productId, String name, String categoryId, String sellerId,
                                int initialStock) {
        super("ProductCreated", productId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.initialStock = initialStock;
    }

    @JsonCreator
    public ProductCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("productId") String productId,
            @JsonProperty("name") String name,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("sellerId") String sellerId,
            @JsonProperty("initialStock") Integer initialStock) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.initialStock = initialStock == null ? 0 : initialStock;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }
    public String getSellerId() { return sellerId; }
    public int getInitialStock() { return initialStock; }
}
