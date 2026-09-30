package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AuctionStartedEvent extends DomainEvent {

    private final String auctionId;

    public AuctionStartedEvent(String auctionId) {
        super("AuctionStarted", auctionId);
        this.auctionId = auctionId;
    }

    @JsonCreator
    public AuctionStartedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
    }

    public String getAuctionId() { return auctionId; }
}
