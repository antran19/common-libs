package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPaidEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Instant paidAt = Instant.now();
        OrderPaidEvent event = new OrderPaidEvent("order-1", "auction-1", "buyer-1", "seller-1",
                new BigDecimal("150.00"), paidAt);

        String json = mapper.writeValueAsString(event);
        OrderPaidEvent parsed = mapper.readValue(json, OrderPaidEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getAuctionId()).isEqualTo("auction-1");
        assertThat(parsed.getBuyerId()).isEqualTo("buyer-1");
        assertThat(parsed.getSellerId()).isEqualTo("seller-1");
        assertThat(parsed.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(parsed.getPaidAt()).isEqualTo(paidAt);
        assertThat(parsed.getEventType()).isEqualTo("OrderPaid");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
    }
}
