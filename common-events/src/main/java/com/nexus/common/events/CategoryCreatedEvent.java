package com.nexus.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CategoryCreatedEvent extends DomainEvent {

    private final String categoryId;
    private final String name;
    private final String parentId;

    public CategoryCreatedEvent(String categoryId, String name, String parentId) {
        super("CategoryCreated", categoryId);
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    @JsonCreator
    public CategoryCreatedEvent(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("occurredAt") Instant occurredAt,
            @JsonProperty("aggregateId") String aggregateId,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("name") String name,
            @JsonProperty("parentId") String parentId) {
        super(eventId, eventType, occurredAt, aggregateId);
        this.categoryId = categoryId;
        this.name = name;
        this.parentId = parentId;
    }

    public String getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getParentId() { return parentId; }
}
