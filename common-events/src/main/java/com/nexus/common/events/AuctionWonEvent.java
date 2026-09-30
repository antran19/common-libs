package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class AuctionWonEvent extends DomainEvent {

    private final String auctionId;
    private final String productId;
    private final String sellerId;
    private final String winnerId;
    private final BigDecimal finalPrice;

    public AuctionWonEvent(String auctionId, String productId, String sellerId, String winnerId, BigDecimal finalPrice) {
        super("AuctionWon", auctionId);
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
    }

    @JsonCreator
    public AuctionWonEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("productId") String productId,
            @JsonProperty("sellerId") String sellerId,
            @JsonProperty("winnerId") String winnerId,
            @JsonProperty("finalPrice") BigDecimal finalPrice) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.auctionId = auctionId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
    }

    public String getAuctionId() { return auctionId; }
    public String getProductId() { return productId; }
    public String getSellerId() { return sellerId; }
    public String getWinnerId() { return winnerId; }
    public BigDecimal getFinalPrice() { return finalPrice; }
}
