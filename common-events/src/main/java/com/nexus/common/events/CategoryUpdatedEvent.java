package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CategoryUpdatedEvent extends DomainEvent {

    private final String categoryId;
    private final String name;

    public CategoryUpdatedEvent(String categoryId, String name) {
        super("CategoryUpdated", categoryId);
        this.categoryId = categoryId;
        this.name = name;
    }

    @JsonCreator
    public CategoryUpdatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("name") String name) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.categoryId = categoryId;
        this.name = name;
    }

    public String getCategoryId() { return categoryId; }
    public String getName() { return name; }
}
