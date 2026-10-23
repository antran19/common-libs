package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// A seller marked an order as shipped. user-service listens to it to learn which (order, buyer,
// seller) triples have really happened, so a rating can be checked against them. sellerId is the
// seller who pressed "shipped": an order has ONE shippedAt, so only that seller is recorded even if
// the order holds items of several sellers. auctionId is null for a direct purchase.
public class OrderShippedEvent extends DomainEvent {

    private final String orderId;
    private final String auctionId;
    private final String buyerId;
    private final String sellerId;
    private final Instant shippedAt;

    public OrderShippedEvent(String orderId, String auctionId, String buyerId, String sellerId, Instant shippedAt) {
        super("OrderShipped", orderId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.shippedAt = shippedAt;
    }

    @JsonCreator
    public OrderShippedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("buyerId") String buyerId,
            @JsonProperty("sellerId") String sellerId,
            @JsonProperty("shippedAt") Instant shippedAt) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.shippedAt = shippedAt;
    }

    public String getOrderId() { return orderId; }
    // null for a direct-purchase order (no auction involved).
    public String getAuctionId() { return auctionId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public Instant getShippedAt() { return shippedAt; }
}
