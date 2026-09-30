package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class AuctionSettledEvent extends DomainEvent {

    private final String auctionId;
    private final String winnerId;
    private final BigDecimal finalPrice;
    private final Instant settledAt;

    public AuctionSettledEvent(String auctionId, String winnerId, BigDecimal finalPrice, Instant settledAt) {
        super("AuctionSettled", auctionId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
        this.settledAt = settledAt;
    }

    @JsonCreator
    public AuctionSettledEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("winnerId") String winnerId,
            @JsonProperty("finalPrice") BigDecimal finalPrice,
            @JsonProperty("settledAt") Instant settledAt) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
        this.settledAt = settledAt;
    }

    public String getAuctionId() { return auctionId; }
    public String getWinnerId() { return winnerId; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public Instant getSettledAt() { return settledAt; }
}
