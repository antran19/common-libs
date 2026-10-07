package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCancelledEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderCancelledEvent event = new OrderCancelledEvent("order-1", "BUYER_REQUESTED");

        String json = mapper.writeValueAsString(event);
        OrderCancelledEvent parsed = mapper.readValue(json, OrderCancelledEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getReason()).isEqualTo("BUYER_REQUESTED");
        assertThat(parsed.getEventType()).isEqualTo("OrderCancelled");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
    }
}
