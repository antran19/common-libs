package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryReservationFailedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InventoryReservationFailedEvent event = new InventoryReservationFailedEvent("order-1", "OUT_OF_STOCK");

        String json = mapper.writeValueAsString(event);
        InventoryReservationFailedEvent parsed = mapper.readValue(json, InventoryReservationFailedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getReason()).isEqualTo("OUT_OF_STOCK");
        assertThat(parsed.getEventType()).isEqualTo("InventoryReservationFailed");
    }
}
