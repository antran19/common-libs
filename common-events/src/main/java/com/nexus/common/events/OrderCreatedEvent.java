package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderCreatedEvent extends DomainEvent {

    private final String orderId;
    private final String auctionId;
    private final String productId;
    private final String sellerId;
    private final String buyerId;
    private final BigDecimal amount;

    public OrderCreatedEvent(String orderId, String auctionId, String productId, String sellerId,
                              String buyerId, BigDecimal amount) {
        super("OrderCreated", orderId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.amount = amount;
    }

    @JsonCreator
    public OrderCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("productId") String productId,
            @JsonProperty("sellerId") String sellerId,
            @JsonProperty("buyerId") String buyerId,
            @JsonProperty("amount") BigDecimal amount) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.amount = amount;
    }

    public String getOrderId() { return orderId; }
    public String getAuctionId() { return auctionId; }
    public String getProductId() { return productId; }
    public String getSellerId() { return sellerId; }
    public String getBuyerId() { return buyerId; }
    public BigDecimal getAmount() { return amount; }
}
