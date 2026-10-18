package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// productId lets catalog-service put the product back on sale and fulfillment-service release
// the unit held for the auction, without either having to remember which product an auction
// was for. Null for events published before it existed.
public class AuctionPaymentTimeoutEvent extends DomainEvent {

    private final String auctionId;
    private final String winnerId;
    private final String productId;

    public AuctionPaymentTimeoutEvent(String auctionId, String winnerId) {
        this(auctionId, winnerId, null);
    }

    public AuctionPaymentTimeoutEvent(String auctionId, String winnerId, String productId) {
        super("AuctionPaymentTimeout", auctionId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.productId = productId;
    }

    @JsonCreator
    public AuctionPaymentTimeoutEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("winnerId") String winnerId,
            @JsonProperty("productId") String productId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.productId = productId;
    }

    public String getAuctionId() { return auctionId; }
    public String getWinnerId() { return winnerId; }
    public String getProductId() { return productId; }
}
