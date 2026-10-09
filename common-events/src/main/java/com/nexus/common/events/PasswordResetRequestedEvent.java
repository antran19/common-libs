package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class PasswordResetRequestedEvent extends DomainEvent {

    private final String userId;
    private final String resetToken;

    public PasswordResetRequestedEvent(String userId, String resetToken) {
        super("PasswordResetRequested", userId);
        this.userId = userId;
        this.resetToken = resetToken;
    }

    @JsonCreator
    public PasswordResetRequestedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("userId") String userId,
            @JsonProperty("resetToken") String resetToken) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.userId = userId;
        this.resetToken = resetToken;
    }

    public String getUserId() { return userId; }
    public String getResetToken() { return resetToken; }
}
