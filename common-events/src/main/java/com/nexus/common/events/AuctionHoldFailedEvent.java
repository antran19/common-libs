package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

// Published by fulfillment-service when it cannot hold a unit for a newly created auction (the
// stock was sold in the few seconds auction-service's copy of the stock lagged behind).
// auction-service cancels the auction in response.
public class AuctionHoldFailedEvent extends DomainEvent {

    private final String auctionId;
    private final String reason;

    public AuctionHoldFailedEvent(String auctionId, String reason) {
        super("AuctionHoldFailed", auctionId);
        this.auctionId = auctionId;
        this.reason = reason;
    }

    @JsonCreator
    public AuctionHoldFailedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("reason") String reason) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.reason = reason;
    }

    public String getAuctionId() { return auctionId; }
    public String getReason() { return reason; }
}
