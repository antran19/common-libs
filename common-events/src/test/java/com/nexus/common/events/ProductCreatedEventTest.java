package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductCreatedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ProductCreatedEvent event = new ProductCreatedEvent("product-1", "Laptop X", "cat-1");

        String json = mapper.writeValueAsString(event);
        ProductCreatedEvent parsed = mapper.readValue(json, ProductCreatedEvent.class);

        assertThat(parsed.getProductId()).isEqualTo("product-1");
        assertThat(parsed.getName()).isEqualTo("Laptop X");
        assertThat(parsed.getEventType()).isEqualTo("ProductCreated");
        assertThat(parsed.getAggregateId()).isEqualTo("product-1");
    }
}
