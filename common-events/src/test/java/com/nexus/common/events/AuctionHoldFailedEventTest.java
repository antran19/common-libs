package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionHoldFailedEventTest {

    @Test
    void roundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        AuctionHoldFailedEvent event = new AuctionHoldFailedEvent("auction-1", "Not enough stock");

        AuctionHoldFailedEvent parsed = mapper.readValue(mapper.writeValueAsString(event), AuctionHoldFailedEvent.class);

        assertThat(parsed.getAuctionId()).isEqualTo("auction-1");
        assertThat(parsed.getReason()).isEqualTo("Not enough stock");
        assertThat(parsed.getEventType()).isEqualTo("AuctionHoldFailed");
        assertThat(parsed.getAggregateId()).isEqualTo("auction-1");
    }
}
