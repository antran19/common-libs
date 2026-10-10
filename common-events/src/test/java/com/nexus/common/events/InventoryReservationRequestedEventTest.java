package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryReservationRequestedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        InventoryReservationRequestedEvent event = new InventoryReservationRequestedEvent("order-1",
                List.of(new ReservationLineItem("sku-1", 2), new ReservationLineItem("sku-2", 1)));

        String json = mapper.writeValueAsString(event);
        InventoryReservationRequestedEvent parsed = mapper.readValue(json, InventoryReservationRequestedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getItems()).containsExactly(new ReservationLineItem("sku-1", 2),
                new ReservationLineItem("sku-2", 1));
        assertThat(parsed.getEventType()).isEqualTo("InventoryReservationRequested");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
    }
}
