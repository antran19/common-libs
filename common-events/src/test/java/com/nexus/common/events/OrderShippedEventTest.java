package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrderShippedEventTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        Instant shippedAt = Instant.parse("2026-10-23T10:15:30Z");
        OrderShippedEvent event = new OrderShippedEvent("order-1", "auction-1", "buyer-1", "seller-1", shippedAt);

        OrderShippedEvent parsed = mapper.readValue(mapper.writeValueAsString(event), OrderShippedEvent.class);

        assertThat(parsed.getEventType()).isEqualTo("OrderShipped");
        assertThat(parsed.getAggregateId()).isEqualTo("order-1");
        assertThat(parsed.getOrderId()).isEqualTo("order-1");
        assertThat(parsed.getAuctionId()).isEqualTo("auction-1");
        assertThat(parsed.getBuyerId()).isEqualTo("buyer-1");
        assertThat(parsed.getSellerId()).isEqualTo("seller-1");
        assertThat(parsed.getShippedAt()).isEqualTo(shippedAt);
    }

    @Test
    void directPurchaseHasNoAuctionId() throws Exception {
        OrderShippedEvent event = new OrderShippedEvent("order-2", null, "buyer-1", "seller-1", Instant.now());

        OrderShippedEvent parsed = mapper.readValue(mapper.writeValueAsString(event), OrderShippedEvent.class);

        assertThat(parsed.getAuctionId()).isNull();
    }

    // A consumer built before a field existed must still read newer payloads: unknown properties
    // are not an error for the plain ObjectMapper the services use via Spring Boot.
    @Test
    void ignoresFieldsAddedLater() throws Exception {
        String json = "{\"eventId\":\"e-1\",\"eventType\":\"OrderShipped\",\"occurredAt\":\"2026-10-23T10:15:30Z\","
                + "\"aggregateId\":\"o-1\",\"orderId\":\"o-1\",\"auctionId\":null,\"buyerId\":\"b\",\"sellerId\":\"s\","
                + "\"shippedAt\":\"2026-10-23T10:15:30Z\"}";

        assertThat(mapper.readValue(json, OrderShippedEvent.class).getSellerId()).isEqualTo("s");
    }
}
