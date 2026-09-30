package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class BidPlacedEvent extends DomainEvent {

    private final String auctionId;
    private final String bidderId;
    private final BigDecimal amount;

    public BidPlacedEvent(String auctionId, String bidderId, BigDecimal amount) {
        super("BidPlaced", auctionId);
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    @JsonCreator
    public BidPlacedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("bidderId") String bidderId,
            @JsonProperty("amount") BigDecimal amount) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public String getAuctionId() { return auctionId; }
    public String getBidderId() { return bidderId; }
    public BigDecimal getAmount() { return amount; }
}
