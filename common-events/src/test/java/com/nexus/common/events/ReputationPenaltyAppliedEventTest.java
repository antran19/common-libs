package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReputationPenaltyAppliedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ReputationPenaltyAppliedEvent event = new ReputationPenaltyAppliedEvent(
                "user-1", "AUCTION_PAYMENT_TIMEOUT", 10, "auction-1");

        String json = mapper.writeValueAsString(event);
        ReputationPenaltyAppliedEvent parsed = mapper.readValue(json, ReputationPenaltyAppliedEvent.class);

        assertThat(parsed.getUserId()).isEqualTo("user-1");
        assertThat(parsed.getReason()).isEqualTo("AUCTION_PAYMENT_TIMEOUT");
        assertThat(parsed.getPoints()).isEqualTo(10);
        assertThat(parsed.getReferenceId()).isEqualTo("auction-1");
        assertThat(parsed.getEventType()).isEqualTo("ReputationPenaltyApplied");
        assertThat(parsed.getAggregateId()).isEqualTo("user-1");
    }
}
