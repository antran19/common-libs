package com.nexus.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegisteredEventTest {

    @Test
    void serializesAndDeserializesRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        UserRegisteredEvent event = new UserRegisteredEvent("user-1", "alice@example.com", "Alice Nguyen");

        String json = mapper.writeValueAsString(event);
        UserRegisteredEvent parsed = mapper.readValue(json, UserRegisteredEvent.class);

        assertThat(parsed.getUserId()).isEqualTo("user-1");
        assertThat(parsed.getEmail()).isEqualTo("alice@example.com");
        assertThat(parsed.getEventType()).isEqualTo("UserRegistered");
        assertThat(parsed.getAggregateId()).isEqualTo("user-1");
    }
}
