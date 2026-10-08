package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ReputationPenaltyAppliedEvent extends DomainEvent {

    private final String userId;
    private final String reason;
    private final int points;
    private final String referenceId;

    public ReputationPenaltyAppliedEvent(String userId, String reason, int points, String referenceId) {
        super("ReputationPenaltyApplied", userId);
        this.userId = userId;
        this.reason = reason;
        this.points = points;
        this.referenceId = referenceId;
    }

    @JsonCreator
    public ReputationPenaltyAppliedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("userId") String userId,
            @JsonProperty("reason") String reason,
            @JsonProperty("points") int points,
            @JsonProperty("referenceId") String referenceId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.userId = userId;
        this.reason = reason;
        this.points = points;
        this.referenceId = referenceId;
    }

    public String getUserId() { return userId; }
    public String getReason() { return reason; }
    public int getPoints() { return points; }
    public String getReferenceId() { return referenceId; }
}
