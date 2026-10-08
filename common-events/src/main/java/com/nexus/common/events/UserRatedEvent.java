package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UserRatedEvent extends DomainEvent {

    private final String ratingId;
    private final String raterId;
    private final String ratedUserId;
    private final String transactionType;
    private final String transactionId;
    private final int score;

    public UserRatedEvent(String ratingId, String raterId, String ratedUserId,
                           String transactionType, String transactionId, int score) {
        super("UserRated", ratingId);
        this.ratingId = ratingId;
        this.raterId = raterId;
        this.ratedUserId = ratedUserId;
        this.transactionType = transactionType;
        this.transactionId = transactionId;
        this.score = score;
    }

    @JsonCreator
    public UserRatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("ratingId") String ratingId,
            @JsonProperty("raterId") String raterId,
            @JsonProperty("ratedUserId") String ratedUserId,
            @JsonProperty("transactionType") String transactionType,
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("score") int score) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.ratingId = ratingId;
        this.raterId = raterId;
        this.ratedUserId = ratedUserId;
        this.transactionType = transactionType;
        this.transactionId = transactionId;
        this.score = score;
    }

    public String getRatingId() { return ratingId; }
    public String getRaterId() { return raterId; }
    public String getRatedUserId() { return ratedUserId; }
    public String getTransactionType() { return transactionType; }
    public String getTransactionId() { return transactionId; }
    public int getScore() { return score; }
}
