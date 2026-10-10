package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryReservedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InventoryReservedEvent event = new InventoryReservedEvent("order-1");

        String json = mapper.writeValueAsString(event);
        InventoryReservedEvent parsed = mapper.readValue(json, InventoryReservedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getEventType()).isEqualTo("InventoryReserved");
    }
}
