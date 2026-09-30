package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AuctionScheduledEvent extends DomainEvent {

    private final String auctionId;
    private final Instant startTime;
    private final Instant endTime;

    public AuctionScheduledEvent(String auctionId, Instant startTime, Instant endTime) {
        super("AuctionScheduled", auctionId);
        this.auctionId = auctionId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @JsonCreator
    public AuctionScheduledEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("startTime") Instant startTime,
            @JsonProperty("endTime") Instant endTime) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getAuctionId() { return auctionId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
}
