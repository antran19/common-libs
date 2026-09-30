package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AuctionPaymentTimeoutEvent extends DomainEvent {

    private final String auctionId;
    private final String winnerId;

    public AuctionPaymentTimeoutEvent(String auctionId, String winnerId) {
        super("AuctionPaymentTimeout", auctionId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
    }

    @JsonCreator
    public AuctionPaymentTimeoutEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("winnerId") String winnerId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.winnerId = winnerId;
    }

    public String getAuctionId() { return auctionId; }
    public String getWinnerId() { return winnerId; }
}
