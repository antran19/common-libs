package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderPaidEvent extends DomainEvent {

    private final String orderId;
    private final String auctionId;
    private final String buyerId;
    private final String sellerId;
    private final BigDecimal amount;
    private final Instant paidAt;

    public OrderPaidEvent(String orderId, String auctionId, String buyerId, String sellerId,
                           BigDecimal amount, Instant paidAt) {
        super("OrderPaid", orderId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    @JsonCreator
    public OrderPaidEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("orderId") String orderId,
            @JsonProperty("auctionId") String auctionId,
            @JsonProperty("buyerId") String buyerId,
            @JsonProperty("sellerId") String sellerId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("paidAt") Instant paidAt) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.orderId = orderId;
        this.auctionId = auctionId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public String getOrderId() { return orderId; }
    public String getAuctionId() { return auctionId; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getPaidAt() { return paidAt; }
}
