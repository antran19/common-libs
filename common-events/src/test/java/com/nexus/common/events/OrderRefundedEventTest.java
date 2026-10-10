package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRefundedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderRefundedEvent event = new OrderRefundedEvent("order-1", "buyer-1", new BigDecimal("100.00"),
                "Customer requested refund");

        String json = mapper.writeValueAsString(event);
        OrderRefundedEvent parsed = mapper.readValue(json, OrderRefundedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getBuyerId()).isEqualTo("buyer-1");
        assertThat(parsed.getAmount()).isEqualByComparingTo("100.00");
        assertThat(parsed.getReason()).isEqualTo("Customer requested refund");
        assertThat(parsed.getEventType()).isEqualTo("OrderRefunded");
    }
}
