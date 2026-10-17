package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

// sellerId and initialStock let fulfillment-service register who owns the product's stock and
// create the initial inventory record without calling catalog-service. Both are optional on
// the wire: events published before they existed deserialize with sellerId = null and
// initialStock = 0, and the 3-argument constructor keeps old callers compiling.
// price lets commerce-service keep its own copy of the selling price, so a cart never trusts a
// price sent by the client. Null for events published before it existed.
public class ProductCreatedEvent extends DomainEvent {

    private final String productId;
    private final String name;
    private final String categoryId;
    private final String sellerId;
    private final int initialStock;
    private final BigDecimal price;

    public ProductCreatedEvent(String productId, String name, String categoryId) {
        this(productId, name, categoryId, null, 0);
    }

    public ProductCreatedEvent(String productId, String name, String categoryId, String sellerId,
                                int initialStock) {
        this(productId, name, categoryId, sellerId, initialStock, null);
    }

    public ProductCreatedEvent(String productId, String name, String categoryId, String sellerId,
                                int initialStock, BigDecimal price) {
        super("ProductCreated", productId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.initialStock = initialStock;
        this.price = price;
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
            @JsonProperty("initialStock") Integer initialStock,
            @JsonProperty("price") BigDecimal price) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.productId = productId;
        this.name = name;
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.initialStock = initialStock == null ? 0 : initialStock;
        this.price = price;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategoryId() { return categoryId; }
    public String getSellerId() { return sellerId; }
    public int getInitialStock() { return initialStock; }
    public BigDecimal getPrice() { return price; }
}
