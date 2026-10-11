package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderReturnReceivedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderReturnReceivedEvent event = new OrderReturnReceivedEvent("order-1",
                List.of(new ReservationLineItem("sku-1", 2), new ReservationLineItem("sku-2", 1)));

        String json = mapper.writeValueAsString(event);
        OrderReturnReceivedEvent parsed = mapper.readValue(json, OrderReturnReceivedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getItems()).containsExactly(new ReservationLineItem("sku-1", 2),
                new ReservationLineItem("sku-2", 1));
        assertThat(parsed.getEventType()).isEqualTo("OrderReturnReceived");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
    }
}
