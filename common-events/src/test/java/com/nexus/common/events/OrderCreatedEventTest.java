package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCreatedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderCreatedEvent event = new OrderCreatedEvent("order-1", "auction-1", "product-1",
                "seller-1", "buyer-1", new BigDecimal("150.00"));

        String json = mapper.writeValueAsString(event);
        OrderCreatedEvent parsed = mapper.readValue(json, OrderCreatedEvent.class);

        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getAuctionId()).isEqualTo("auction-1");
        assertThat(parsed.getProductId()).isEqualTo("product-1");
        assertThat(parsed.getSellerId()).isEqualTo("seller-1");
        assertThat(parsed.getBuyerId()).isEqualTo("buyer-1");
        assertThat(parsed.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(parsed.getEventType()).isEqualTo("OrderCreated");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
    }

    @Test
    void auctionIdIsNullForDirectPurchaseOrders() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        OrderCreatedEvent event = new OrderCreatedEvent("order-2", null, "product-2",
                "seller-2", "buyer-2", new BigDecimal("50.00"));

        String json = mapper.writeValueAsString(event);
        OrderCreatedEvent parsed = mapper.readValue(json, OrderCreatedEvent.class);

        assertThat(parsed.getAuctionId()).isNull();
    }
}
