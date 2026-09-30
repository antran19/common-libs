package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class OutbidEvent extends DomainEvent {

    private final String auctionId;
    private final String outbidBidderId;
    private final BigDecimal newHighestBid;

    public OutbidEvent(String auctionId, String outbidBidderId, BigDecimal newHighestBid) {
        super("Outbid", auctionId);
        this.auctionId = auctionId;
        this.outbidBidderId = outbidBidderId;
        this.newHighestBid = newHighestBid;
    }

    @JsonCreator
    public OutbidEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("outbidBidderId") String outbidBidderId,
            @JsonProperty("newHighestBid") BigDecimal newHighestBid) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.outbidBidderId = outbidBidderId;
        this.newHighestBid = newHighestBid;
    }

    public String getAuctionId() { return auctionId; }
    public String getOutbidBidderId() { return outbidBidderId; }
    public BigDecimal getNewHighestBid() { return newHighestBid; }
}
