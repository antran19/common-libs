package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRatedEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        UserRatedEvent event = new UserRatedEvent("rating-1", "rater-1", "rated-1", "ORDER", "order-1", 5);

        String json = mapper.writeValueAsString(event);
        UserRatedEvent parsed = mapper.readValue(json, UserRatedEvent.class);

        assertThat(parsed.getRatingId()).isEqualTo("rating-1");
        assertThat(parsed.getRaterId()).isEqualTo("rater-1");
        assertThat(parsed.getRatedUserId()).isEqualTo("rated-1");
        assertThat(parsed.getTransactionType()).isEqualTo("ORDER");
        assertThat(parsed.getTransactionId()).isEqualTo("order-1");
        assertThat(parsed.getScore()).isEqualTo(5);
        assertThat(parsed.getEventType()).isEqualTo("UserRated");
        assertThat(parsed.getAggregateId()).isEqualTo("rating-1");
    }
}
