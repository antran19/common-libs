package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCreatedEventTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void serializesAndDeserializesRoundTrip_withSellerAndInitialStock() throws Exception {
        ProductCreatedEvent event = new ProductCreatedEvent("p-1", "Watch", "cat-1", "seller-1", 25);

        ProductCreatedEvent parsed = mapper.readValue(mapper.writeValueAsString(event), ProductCreatedEvent.class);

        assertThat(parsed.getProductId()).isEqualTo("p-1");
        assertThat(parsed.getSellerId()).isEqualTo("seller-1");
        assertThat(parsed.getInitialStock()).isEqualTo(25);
        assertThat(parsed.getEventType()).isEqualTo("ProductCreated");
        assertThat(parsed.getAggregateId()).isEqualTo("p-1");
    }

    @Test
    void threeArgumentConstructor_meansNoSellerAndNoInitialStock() {
        ProductCreatedEvent event = new ProductCreatedEvent("p-1", "Watch", "cat-1");

        assertThat(event.getSellerId()).isNull();
        assertThat(event.getInitialStock()).isZero();
    }

    @Test
    void deserializesAnOldEventThatHasNoSellerOrStockFields() throws Exception {
        String oldJson = "{\"eventId\":\"e-1\",\"eventType\":\"ProductCreated\",\"occurredAt\":\"2026-10-01T00:00:00Z\","
                + "\"aggregateId\":\"p-1\",\"productId\":\"p-1\",\"name\":\"Watch\",\"categoryId\":\"cat-1\"}";

        ProductCreatedEvent parsed = mapper.readValue(oldJson, ProductCreatedEvent.class);

        assertThat(parsed.getProductId()).isEqualTo("p-1");
        assertThat(parsed.getSellerId()).isNull();
        assertThat(parsed.getInitialStock()).isZero();
    }
}
