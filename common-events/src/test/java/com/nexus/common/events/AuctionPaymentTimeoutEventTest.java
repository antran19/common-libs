package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionPaymentTimeoutEventTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void roundTrip_carriesTheProduct() throws Exception {
        AuctionPaymentTimeoutEvent event = new AuctionPaymentTimeoutEvent("auction-1", "winner-1", "product-1");

        AuctionPaymentTimeoutEvent parsed =
                mapper.readValue(mapper.writeValueAsString(event), AuctionPaymentTimeoutEvent.class);

        assertThat(parsed.getAuctionId()).isEqualTo("auction-1");
        assertThat(parsed.getWinnerId()).isEqualTo("winner-1");
        assertThat(parsed.getProductId()).isEqualTo("product-1");
    }

    @Test
    void anOldEventWithoutProduct_stillReads() throws Exception {
        String oldJson = "{\"eventId\":\"e-1\",\"eventType\":\"AuctionPaymentTimeout\",\"occurredAt\":\"2026-10-01T00:00:00Z\","
                + "\"aggregateId\":\"auction-1\",\"auctionId\":\"auction-1\",\"winnerId\":\"winner-1\"}";

        assertThat(mapper.readValue(oldJson, AuctionPaymentTimeoutEvent.class).getProductId()).isNull();
        assertThat(new AuctionPaymentTimeoutEvent("auction-1", "winner-1").getProductId()).isNull();
    }
}
