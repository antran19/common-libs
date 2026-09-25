package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class UserRegisteredEvent extends DomainEvent {

    private final String userId;
    private final String email;
    private final String fullName;

    public UserRegisteredEvent(String userId, String email, String fullName) {
        super("UserRegistered", userId);
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }

    @JsonCreator
    public UserRegisteredEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("userId") String userId,
            @JsonProperty("email") String email,
            @JsonProperty("fullName") String fullName) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}
