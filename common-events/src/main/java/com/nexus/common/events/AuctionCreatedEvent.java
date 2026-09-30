package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AuctionCreatedEvent extends DomainEvent {

    private final String auctionId;
    private final String productId;
    private final String sellerId;

    public AuctionCreatedEvent(String auctionId, String productId, String sellerId) {
        super("AuctionCreated", auctionId);
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
    }

    @JsonCreator
    public AuctionCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("productId") String productId,
            @JsonProperty("sellerId") String sellerId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
    }

    public String getAuctionId() { return auctionId; }
    public String getProductId() { return productId; }
    public String getSellerId() { return sellerId; }
}
