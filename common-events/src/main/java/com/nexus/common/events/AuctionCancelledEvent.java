package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AuctionCancelledEvent extends DomainEvent {

    private final String auctionId;
    private final String cancelledBy;

    public AuctionCancelledEvent(String auctionId, String cancelledBy) {
        super("AuctionCancelled", auctionId);
        this.auctionId = auctionId;
        this.cancelledBy = cancelledBy;
    }

    @JsonCreator
    public AuctionCancelledEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("cancelledBy") String cancelledBy) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.cancelledBy = cancelledBy;
    }

    public String getAuctionId() { return auctionId; }
    public String getCancelledBy() { return cancelledBy; }
}
